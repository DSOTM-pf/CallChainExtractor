package com.neu.lab.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LogUtil {
    private LogUtil() {}

    private static Map<Class<?>, Logger> logger = new HashMap<>();

    private static Logger getLogger(Object obj) {
        Class clazz;
        if(obj instanceof Class)
            clazz = (Class)obj;
        else
            clazz = obj.getClass();
        if(logger.containsKey(clazz))
            return logger.get(clazz);
        Logger log = LoggerFactory.getLogger(clazz);
        logger.put(clazz, log);
        return log;
    }


    public static void trace(Object obj, String format, Object... arguments){
        getLogger(obj).trace(format, arguments);
    }

    public static void debug(Object obj, String format, Object... arguments){
        getLogger(obj).debug(format, arguments);
    }

    public static void info(Object obj, String format, Object... arguments){
        getLogger(obj).info(format, arguments);
    }

    public static void warn(Object obj, String format, Object... arguments){
        getLogger(obj).warn(format, arguments);
    }

    public static void error(Object obj, String format, Object... arguments){
        getLogger(obj).error(format, arguments);
    }
}
