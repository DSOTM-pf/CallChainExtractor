package com.neu.lab;

import com.neu.lab.analyzer.CGAnalyzer;
import com.neu.lab.analyzer.MethodAnalyzer;
import com.neu.lab.util.FileUtil;
import com.neu.lab.util.LogUtil;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * Created by 17921 on 2020/10/28
 */
public class cfgTest {
    static MethodAnalyzer methodAnalyzer ;
    @Test
    public void testMain() throws IOException, XmlPullParserException {
        // Initialize Soot

        Config.get().apkFile = Paths.get("apks/hello.apk");
        Config.get().androidJarDir = Paths.get("D:\\AndroidEnviorment\\androidJAR");
        Config.get().init();

        LogUtil.info(CallChainMain.class, "Start analyzing apk for package {}", Config.get().apkPackageName);
        //
        CGAnalyzer cgAnalyzer = CGAnalyzer.get(Config.get().inputFmt);
        LogUtil.info(CallChainMain.class, "Start constructing call graph");

        //CallGraph
        CallGraph callGraph = cgAnalyzer.getCallGraph(Config.get().cgAlgo);
        methodAnalyzer = MethodAnalyzer.get(callGraph);
        methodAnalyzer.test2019();
        // Iterate over the callgraph
/*        for (Iterator<Edge> edgeIt = callGraph.iterator(); edgeIt.hasNext(); ) {
            Edge edge = edgeIt.next();

            SootMethod smSrc = edge.src();
            Unit uSrc = edge.srcStmt();
            SootMethod smDest = edge.tgt();
            FileUtil.appendStringTo(smSrc + "  =>>>  " + smDest+"\n","test.txt");
            System.out.println(smSrc + "  =>>>  " + smDest+"\n");
        }*/
    }
}
