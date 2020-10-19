package com.neu.lab.analyzer;


import com.neu.lab.Config;
import com.neu.lab.Global;
import com.neu.lab.entity.CallChain;
import com.neu.lab.entity.JClass;
import com.neu.lab.entity.JMethod;
import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.Sources;

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

//    private Map<JMethod, SootMethod> applicationMethods = new HashMap<>();
    //    private Map<JMethod,Set<APermission>> apiToDangerousPermissions = new HashMap<>();
    private Set<JMethod> apkMethods = new HashSet<>();
    private Set<CallChain> dangerousCallChains = new HashSet<>();

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
    public void getApkMethods() {
        for (Edge edge : cg) {
            String tgtSig = edge.getTgt().method().getSignature();
            JMethod tgtMethod = JMethod.fromSootSignature(tgtSig);
            apkMethods.add(tgtMethod);
        }
    }


    /**
     * 寻找所有方法的CallChain
     * ？如何确定最底部的方法
     */
    public Set<CallChain> getAllCallchains() {
/*        if (applicationMethods.size() == 0)
            obtainApplicationMethods();*/
        getApkMethods();
        for(JMethod api:apkMethods)
        {
            LinkedList<JMethod> callChain = new LinkedList<>();
            callChain.addFirst(api);
            travelCallGraph(callChain, new HashSet<>());
        }
        return dangerousCallChains;
    }

    private void travelCallGraph(LinkedList<JMethod> chain, Set<JMethod> visited) {
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
                    JMethod api = chain.getLast();
                    CallChain cc = new CallChain(chain); //temp
                    dangerousCallChains.add(cc);
                }
            } else if (!visited.contains(nextMethod)) {
                chain.addFirst(nextMethod);
                travelCallGraph(chain, visited);
                chain.removeFirst();
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

/*    private void obtainApplicationMethods() {
        for (SootClass sootClass : Scene.v().getApplicationClasses()) {
            for (SootMethod sootMethod : sootClass.getMethods()) {
                JMethod method = JMethod.fromSootSignature(sootMethod.getSignature());
                applicationMethods.put(method, sootMethod);
            }
        }
    }*/
}