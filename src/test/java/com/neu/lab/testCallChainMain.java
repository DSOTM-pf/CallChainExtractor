package com.neu.lab;

import com.neu.lab.entity.CallChain;
import com.neu.lab.entity.DCallChain;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Created by 17921 on 2020/10/18
 */
public class testCallChainMain {
    private  CallChainMain callChainMain;

    @Test
    public void testMain() throws IOException, XmlPullParserException {
        callChainMain = new CallChainMain();
        Set<CallChain> results = callChainMain.getAllChains(Paths.get("apks/sealnote.apk"),Paths.get("D:\\AndroidEnviorment\\androidJAR"));
        System.out.println("Done");
    }
}
