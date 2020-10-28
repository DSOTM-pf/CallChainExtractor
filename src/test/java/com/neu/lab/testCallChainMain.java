package com.neu.lab;

import com.neu.lab.entity.CallChain;
import com.neu.lab.entity.JMethod;
import com.neu.lab.util.FileUtil;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Created by 17921 on 2020/10/18
 */
public class testCallChainMain {
    @Test
    public void testMain() throws IOException, XmlPullParserException {

        Set<CallChain> resultsOfALl = CallChainMain.getAllChains(Paths.get("apks/mapbox.apk"),Paths.get("D:\\AndroidEnviorment\\androidJAR"));
//        Set<CallChain> resultsOfAPP = CallChainMain.getAppChains(Paths.get("apks/mapbox.apk"),Paths.get("D:\\AndroidEnviorment\\androidJAR"));

        FileUtil.writeCallchainsTo(resultsOfALl,"resultsOfALl.txt");
//        FileUtil.writeCallchainsTo(resultsOfAPP,"resultsOfAPP.txt");

    }
}
