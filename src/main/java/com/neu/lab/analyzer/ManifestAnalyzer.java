package com.neu.lab.analyzer;

import org.xmlpull.v1.XmlPullParserException;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 1: is targetSDKVersion < 23 ?
 * 2：get dangerous permission in AndroidManifest.xml
 *
 */
public class ManifestAnalyzer extends ProcessManifest {
    private ManifestAnalyzer(Path apkPath) throws IOException, XmlPullParserException {
        super(apkPath.toString());
    }

    private static ManifestAnalyzer singleton = null;

    public static ManifestAnalyzer get(Path apkPath) throws IOException, XmlPullParserException {
        if(singleton == null)
            singleton = new ManifestAnalyzer(apkPath);
        return singleton;
    }

    public static ManifestAnalyzer get() {
        if(singleton == null)
            throw new InstantiationError("ManifestAnalyzer not initialized");
        return singleton;
    }

}
