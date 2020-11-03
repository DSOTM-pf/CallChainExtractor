package com.neu.lab;

import soot.SootMethod;

/**
 * Created by 17921 on 2020/11/1
 */
public class ExcludeMethod
{

    public static boolean excludeMethod(SootMethod methodName)
    {
        String pkg = methodName.getDeclaringClass().getPackageName();

        return (pkg.contains("java.lang")
                || pkg.contains("java.util") || pkg.contains("sun.security")
                || pkg.contains("java.security") || pkg.contains("sun.reflect")
                || pkg.contains("sun.net") || pkg.contains("java.nio")
                || pkg.contains("sun.misc") || pkg.contains("java.nio"));
    }
}
