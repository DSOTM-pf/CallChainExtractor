package com.neu.lab.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JMethod {
    private String name;
    private String methodName;
    private JClass class_;
    private JClass retClass;
    private JClass[] paramClasses;

    public JMethod(String methodName, JClass class_, JClass retClass, JClass[] paramClasses) {
        this.methodName = methodName;
        this.class_ = class_;
        this.retClass = retClass;
        this.paramClasses = paramClasses;
        obtainFullName();
    }

    public String getName(){
        return name;
    }

    public String getMethodName() {
        return methodName;
    }
    
    public JClass getDefiningClass() {
        return class_;
    }

    private void obtainFullName(){
        String[] paramStrings = new String[paramClasses.length];
        for(int i = 0; i < paramClasses.length; i++) {
            paramStrings[i] = paramClasses[i].toString();
        }
        String params = String.join(",", paramStrings);
        name = String.format("%s.%s(%s)%s", class_, methodName, params, retClass);
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }

    @Override
    public boolean equals(Object object){
        if(object instanceof String)
            return name.equals(object);
        else if(object instanceof JMethod)
            return name.equals(((JMethod)object).getName());
        return false;
    }

    @Override
    public String toString(){
        return name;
    }

    public String toSootSignature() {
        String[] paramStrings = new String[paramClasses.length];
        for(int i = 0; i < paramClasses.length; i++) {
            paramStrings[i] = paramClasses[i].toString();
        }
        String params = String.join(",", paramStrings);
        return String.format("<%s: %s %s(%s)>", class_, retClass, methodName, params);
    }

    private static final Pattern SOOT_METHOD_PATTERN = Pattern.compile("^<(.+?): (.+?) (.+?)\\((.*?)\\)>$");
    public static JMethod fromSootSignature(String sig) {
        Matcher m = SOOT_METHOD_PATTERN.matcher(sig);
        if(m.find()) {
            String method = m.group(3);
            JClass class_ = new JClass(m.group(1));
            JClass retClass = new JClass(m.group(2));
            String[] params = m.group(4).split(",");
            JClass[] paramClasses = new JClass[params.length];
            for(int i = 0; i < params.length; i++){
                paramClasses[i] = new JClass(params[i]);
            }
            return new JMethod(method, class_, retClass, paramClasses);
        }
        else {
            throw new IllegalArgumentException("not a valid method: "+sig);
        }
    }

    private static final Pattern AXP_METHOD_PATTERN = Pattern.compile("^(.*?)\\.(\\w+)\\((.*?)\\)(.*?)$");
    public static JMethod fromAxplorerSignature(String sig) {
        Matcher m = AXP_METHOD_PATTERN.matcher(sig);
        if(m.find()) {
            String method = m.group(2);
            JClass class_ = new JClass(m.group(1));
            JClass retClass = new JClass(m.group(4));
            String[] params = m.group(3).split(",");
            JClass[] paramClasses = new JClass[params.length];
            for(int i = 0; i < params.length; i++){
                paramClasses[i] = new JClass(params[i]);
            }
            return new JMethod(method, class_, retClass, paramClasses);
        }
        else {
            throw new IllegalArgumentException("not a valid method: "+sig);
        }
    }
}
