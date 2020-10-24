package com.neu.lab;

import com.neu.lab.analyzer.CGAnalyzer;
import com.neu.lab.analyzer.MethodAnalyzer;
import com.neu.lab.entity.CallChain;
import com.neu.lab.util.FileUtil;
import com.neu.lab.util.LogUtil;
import org.xmlpull.v1.XmlPullParserException;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by 17921 on 2020/10/18
 */
public  class CallChainMain {
    static MethodAnalyzer methodAnalyzer ;
    public  static void pre(Path apkFile,Path androidJars) throws IOException, XmlPullParserException {
        Config.get().apkFile = apkFile;
        Config.get().androidJarDir = androidJars;
        Config.get().init();

        LogUtil.info(CallChainMain.class, "Start analyzing apk for package {}", Config.get().apkPackageName);
        //
        CGAnalyzer cgAnalyzer = CGAnalyzer.get(Config.get().inputFmt);
        LogUtil.info(CallChainMain.class, "Start constructing call graph");
        //CallGraph
        CallGraph callGraph = cgAnalyzer.getCallGraph(Config.get().cgAlgo);

        LogUtil.info(CallChainMain.class, "Call graph generated with {} algorithm", Config.get().cgAlgo);
//        cgAnalyzer.saveCallGraphToFile(Config.get().cgAlgo);
        //CallChain
        methodAnalyzer = MethodAnalyzer.get(callGraph);

    }

    public  static Set<CallChain> getAppChains(Path apkFile,Path androidJars) throws IOException, XmlPullParserException {
        pre(apkFile,androidJars);
        LogUtil.info(CallChainMain.class, "Start extracting methods (method analysis)");
        Set<CallChain> allCallchains = methodAnalyzer.getApplicationCallChains();
        LogUtil.info(CallChainMain.class, "Extracted {}  api-call-chains", allCallchains.size());

        return allCallchains;
    }

    public  static Set<CallChain> getAllChains(Path apkFile,Path androidJars) throws IOException, XmlPullParserException {
        pre(apkFile,androidJars);
        Set<CallChain> allCallchains = methodAnalyzer.getAllCallchains();
        LogUtil.info(CallChainMain.class, "Extracted {}  api-call-chains", allCallchains.size());

        return allCallchains;
    }
}
