package com.neu.lab.analyzer;


import com.neu.lab.Config;
import com.neu.lab.util.FileUtil;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.InfoflowConfiguration.CallgraphAlgorithm;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.config.SootConfigForAndroid;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Sources;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CGAnalyzer {

    private int inputFormat;
    private Map<String,CallGraph> cgMap;

    private CGAnalyzer(String inputFormat) {
        switch (inputFormat.toLowerCase()){
            case "apk":
                this.inputFormat = Options.src_prec_apk;
                break;
            case "src":
                this.inputFormat = Options.src_prec_java;
                break;
            default:
                throw new IllegalArgumentException("Unsupported input format "+inputFormat);
        }
        cgMap = new HashMap<>();
    }

    private static CGAnalyzer singleton = null;

    public static CGAnalyzer get(String inputFormat) {
        if(singleton == null)
            singleton = new CGAnalyzer(inputFormat);
        return singleton;
    }

    public static CGAnalyzer get() {
        if(singleton == null)
            throw new InstantiationError("CGAnalyzer not initialized");
        return singleton;
    }

    private CallgraphAlgorithm getCGAlgorithm(String algo) {
        switch (algo) {
            case "cha": //recommended
                //https://github.com/secure-software-engineering/FlowDroid/issues/64#issuecomment-419834271
                return CallgraphAlgorithm.CHA;
            case "geom":
                return CallgraphAlgorithm.GEOM;
            case "rta":
                return CallgraphAlgorithm.RTA;
            case "vta":
                return CallgraphAlgorithm.VTA;
            case "spark":
            default:
                return CallgraphAlgorithm.SPARK;
        }
    }

    public CallGraph getCallGraph(String cgType) {
        String key = cgType.toLowerCase();
        if(cgMap.containsKey(key)){ // 如果已经构建过某个算法类型的Callgraph，则直接返回
            return cgMap.get(key);
        }
        else {
            CallgraphAlgorithm algo = getCGAlgorithm(key);
            SetupApplication application = initCgConfig(algo);
            application.constructCallgraph();
//            application.getDummyMainMethod();
            CallGraph callGraph = Scene.v().getCallGraph();
            cgMap.put(key, callGraph);
            return callGraph;
        }
    }

    public Iterator<SootMethod> getCallTo(SootMethod method) {
        CallGraph cg = getCallGraph(Config.get().cgAlgo);
        Iterator<MethodOrMethodContext> parents = new Sources(cg.edgesInto(method));
        return new Iterator<SootMethod>() {
            @Override
            public boolean hasNext() {
                return parents.hasNext();
            }
            @Override
            public SootMethod next() {
                return parents.next().method();
            }
        };
    }

    public Iterator<SootMethod> getCallFrom(SootMethod method) {
        CallGraph cg = getCallGraph(Config.get().cgAlgo);
        Iterator<MethodOrMethodContext> children = new Targets(cg.edgesOutOf(method));
        return new Iterator<SootMethod>() {
            @Override
            public boolean hasNext() {
                return children.hasNext();
            }
            @Override
            public SootMethod next() {
                return children.next().method();
            }
        };
    }

    public void saveCallGraphToFile(String cgType) {
        String key = cgType.toLowerCase();
        if(cgMap.containsKey(key)){
            CallGraph cg = cgMap.get(key);
//            FileUtil.writeStringTo(cg.toString(), "appCallGraph-"+key+".txt");
        }
        else {
            CallGraph cg = getCallGraph(cgType);
//            FileUtil.writeStringTo(cg.toString(), "appCallGraph-"+key+".txt");
        }
    }


    private SetupApplication initCgConfig(CallgraphAlgorithm cgAlgo) {
        SetupApplication application = new SetupApplication(Config.get().androidJarDir.toString(),
                                                            Config.get().apkFile.toString());
        soot.G.reset();
        SootConfigForAndroid sootConf = new SootConfigForAndroid() {
            @Override
            public void setSootOptions(Options options, InfoflowConfiguration config) {
                super.setSootOptions(options, config);
                config.setCallgraphAlgorithm(cgAlgo);
                Options.v().set_allow_phantom_refs(true);
                Options.v().set_whole_program(true);
                Options.v().set_prepend_classpath(true);
                Options.v().set_process_multiple_dex(true);
                Options.v().set_validate(true);
                Options.v().set_force_android_jar(Config.get().versionSdkFile.toString());
                Options.v().set_soot_classpath(Config.get().versionSdkFile.toString());
                Options.v().set_process_dir(Collections.singletonList(Config.get().apkFile.toString()));
                Options.v().set_src_prec(inputFormat);
                Options.v().set_output_format(Options.output_format_dex);
            }
        };
        application.setSootConfig(sootConf);
        application.setCallbackFile(Config.get().androidCallbacksFile.toString());
        Scene.v().loadNecessaryClasses();
        return application;
    }

}
