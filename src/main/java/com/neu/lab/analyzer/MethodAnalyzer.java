package com.neu.lab.analyzer;


import com.neu.lab.Config;
import com.neu.lab.Global;
import com.neu.lab.entity.CallChain;
import com.neu.lab.entity.JClass;
import com.neu.lab.entity.JMethod;
import com.neu.lab.util.FileUtil;
import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.Sources;

import java.io.File;
import java.util.*;

/**
 * get entry and caller
 * 1：获取应用内使用到权限相关的方法
 * 2：获取应用内使用危险权限相关的方法
 * 3：根据上述方法，获取到caller
 */
public class MethodAnalyzer {
    private static final Set<JClass> ENTRIES = new HashSet<>(Arrays.asList(
            new JClass("java.lang.Thread"),
            new JClass("dummyMainClass")
    ));

    /**
     * Terms:
     * API: apis in Android sdk
     * CALLER: app/lib methods that invoke apis
     * METHOD: api & caller
     * with ALL: all specified in sdk
     */

    private Set<JMethod> applicationMethods = new HashSet<>();
    //    private Map<JMethod,Set<APermission>> apiToDangerousPermissions = new HashMap<>();
    private Set<JMethod> allMethods = new HashSet<>();

    private CallGraph cg;

    private MethodAnalyzer(CallGraph cg) {
        this.cg = cg;
    }

    private static MethodAnalyzer singleton = null;

    public static MethodAnalyzer get(CallGraph cg) {
        if (singleton == null)
            singleton = new MethodAnalyzer(cg);
        return singleton;
    }

    public static MethodAnalyzer get() {
        if (singleton == null)
            throw new InstantiationError("MethodAnalyzer not initialized");
        return singleton;
    }

    /*
        public Set<JMethod> getApkMethods()
        {
            Set<JMethod> methodSet = new HashSet<>();
            for(Edge edge:cg)
            {
                String tgtSig = edge.getTgt().method().getSignature();
                JMethod tgtMethod = JMethod.fromSootSignature(tgtSig);
                methodSet.add(tgtMethod);
            }
            return methodSet;
        }
    */
    public void getApplicationMethods() {
        //app methods属于Android的方法
//        Scene.v().getBasicClasses()
//        Scene.v().getPhantomClasses()
//        Scene.v().getLibraryClasses()
//        Scene.v().getApplicationClasses()

        for (SootClass sootClass : Scene.v().getApplicationClasses()) {
            for (SootMethod sootMethod : sootClass.getMethods()) {
                if (isBottom(sootMethod)) {
                    JMethod applicationMethod = JMethod.fromSootSignature(sootMethod.getSignature());
                    applicationMethods.add(applicationMethod);
                }
            }
        }
    }

    /**
     * CallGraph all method
     */
    public void getCGMethods() {
        for (Edge edge : cg) {
            if (isBottom(edge.getTgt().method())) {
                String tgtSig = edge.getTgt().method().getSignature();
                JMethod tgtMethod = JMethod.fromSootSignature(tgtSig);
                allMethods.add(tgtMethod);
            }
        }
    }

    private void methodInit() {
        getApplicationMethods();
        getCGMethods();
    }

    private boolean isBottom(SootMethod sootMethod) {

        Iterator<MethodOrMethodContext> ptargets = new Sources(cg.edgesInto(sootMethod));
        if (ptargets.next() == null) {
            return false;
        }
        return true;
    }

    public Set<CallChain> getApplicationCallChains() {
        Set<CallChain> callChains = new HashSet<>();
        callChains.clear();
        methodInit();
        int i = 1;
        System.out.println("All Method：" + allMethods.size());
        for (JMethod api : allMethods) {
            //判断API是不是谷底
            if (applicationMethods.contains(api)) {
                LinkedList<JMethod> callChain = new LinkedList<>();
                callChain.addFirst(api);
                travelCallGraph(callChain, new HashSet<>(), callChains);
            }
            System.out.println("Now：" + i);
            i++;
        }
        return callChains;
    }

    /**
     * 寻找所有方法的CallChain
     * ？如何确定最底部的方法
     */
    public Set<CallChain> getAllCallchains() {
        Set<CallChain> callChains = new HashSet<>();
        callChains.clear();
        methodInit();
        int i = 1;
        System.out.println("All Method：" + allMethods.size());
        for (JMethod api : allMethods) {
            LinkedList<JMethod> callChain = new LinkedList<>();
            callChain.addFirst(api);
            travelCallGraph(callChain, new HashSet<>(), callChains);
            System.out.println("Now：" + i);
            i++;
        }
        return callChains;
    }

    private boolean isInSet(CallChain callChain, Set<CallChain> callChains) {
        for (CallChain pre : callChains) {
            if (callChain.equals(pre)) return true;
        }
        return false;
    }

    private void travelCallGraph(LinkedList<JMethod> chain, Set<JMethod> visited, Set<CallChain> callChains) {
        JMethod method = chain.getFirst();
        visited.add(method);

        // iterate over unvisited parents
        // https://github.com/secure-software-engineering/soot-infoflow-android/issues/155#issuecomment-344506022
        SootMethod sootMethod = Scene.v().getMethod(method.toSootSignature());
        Iterator<MethodOrMethodContext> parents = new Sources(cg.edgesInto(sootMethod));

        parents.forEachRemaining(p -> {
            JMethod nextMethod = JMethod.fromSootSignature(p.method().getSignature());
            if (reachDummy(nextMethod) || reachSdk(nextMethod)) {
                if (chain.size() > 1) {  // cannot be a single method
                    JMethod api = chain.getFirst();
                    if (!api.getName().startsWith("java.lang") && !api.getName().startsWith("org.xmlpull")) {
                        CallChain cc = new CallChain(chain); //temp
                        if (!isInSet(cc, callChains)) callChains.add(cc);
                    }
//                        callChains.add(cc);}

                }
            } else if (!visited.contains(nextMethod)) {
                chain.addFirst(nextMethod);
                travelCallGraph(chain, visited, callChains);
                chain.removeFirst();//dummy method
            }
        });
    }

    private boolean reachDummy(JMethod parent) {
        return ENTRIES.contains(parent.getDefiningClass());
    }

    private boolean reachSdk(JMethod parent) {
        String parentClass = parent.getDefiningClass().getName();
        for (String black : Global.BLACK_LIST)
            if (parentClass.startsWith(black))
                return true;
        return false;
    }

    public void test2018() {
        for (Edge edge : cg) {
            //这个是判断java的 edge.src().isEntryMethod()
            if (isDummyMain(edge.src())) {
                System.out.println(edge);
            }
        }
    }

    private boolean isDummyMain(SootMethod sootMethod) {
        if (sootMethod.toString().contains("dummyMainClass")) {
            return true;
        }
        return false;
    }
/*    private void obtainApplicationMethods() {
        for (SootClass sootClass : Scene.v().getApplicationClasses()) {
            for (SootMethod sootMethod : sootClass.getMethods()) {
                JMethod method = JMethod.fromSootSignature(sootMethod.getSignature());
                applicationMethods.put(method, sootMethod);
            }
        }
    }*/
}