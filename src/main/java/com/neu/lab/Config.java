package com.neu.lab;

import com.neu.lab.analyzer.ManifestAnalyzer;
import com.neu.lab.util.FileUtil;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by 17921 on 2020/10/15
 */
public class Config {
    //需要指定的信息
    public Path apkFile;
    public Path androidJarDir;
    private Path outputDir = Paths.get("analyzerOutput/");
    public Path assetDir = Paths.get("res");
    public String cgAlgo = "CHA";
    public String inputFmt = "apk";

    public String apkPackageName = null;
    public Path apkOutputDir = null;
    public int apkTargetVersion = 0;
    public Path versionDangerousFile = null;
    public Path versionSdkFile = null;
    public Path androidCallbacksFile = null;

    public void dataInit() {
        apkFile = Paths.get("apks/sealnote.apk");
        androidJarDir = Paths.get("D:\\AndroidEnviorment\\androidJAR");
    }

    public void init() throws IOException, XmlPullParserException {
        apkPackageName = ManifestAnalyzer.get(apkFile).getPackageName();
        apkOutputDir = outputDir.resolve(apkPackageName);
        apkTargetVersion = ManifestAnalyzer.get().targetSdkVersion();// target sdk version of apk

        versionSdkFile = androidJarDir.resolve("android-" + apkTargetVersion).resolve("android.jar");
        androidCallbacksFile = assetDir.resolve("AndroidCallbacks.txt");
//        FileUtil.writeStringTo("Target version: " + apkTargetVersion, "target.txt");

    }


    private Config() {
    }

    private static Config singleton = null;

    public static Config get() {
        if (singleton == null)
            singleton = new Config();
        return singleton;
    }
}
