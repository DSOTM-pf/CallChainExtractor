package com.neu.lab.util;

import com.neu.lab.Config;
import com.neu.lab.entity.CallChain;
import com.neu.lab.entity.JMethod;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class FileUtil {

    private static final String ENCODING = "UTF-8";


    private FileUtil(){}


    public static List<String> readLinesFrom(String filePath) throws IOException {
        File file = new File(filePath);
        return FileUtils.readLines(file, ENCODING);
    }



    public static void writeSetTo(Set<?> set, String filePath) {
        writeCollection(set, filePath);
    }

    public static void writeListTo(List<?> list, String filePath) {
        writeCollection(list, filePath);
    }


    public static void writeApisToCallersTo(List<JMethod> apis, List<JMethod> callers, String filePath) {
        List<String> strings = new LinkedList<>();
        for(int i = 0; i < apis.size(); i++) {
            String line = String.format("%s   ===>   %s", apis.get(i), callers.get(i));
            strings.add(line);
        }
        writeCollection(strings, filePath);
    }

    public static void writeCallchainsTo(Set<CallChain> chains, String filePath) {
        List<String> strings = new LinkedList<>();
        for(CallChain cc: chains) {
            List<JMethod> chain = cc.getChain();
            StringBuilder sb = new StringBuilder();
            sb.append(chain.get(0));
            sb.append("\n");
            for(int i = 1; i < chain.size(); i++) {
                String lead = String.format("%"+i+"c", ' ');
                sb.append(lead);
                sb.append(chain.get(i));
                sb.append("\n");
            }
            strings.add(sb.toString());
        }
        writeCollection(strings, filePath);
    }

    public static void writeStringTo(String string, String filePath) {
        writeContent(string, filePath);
    }

    public static void appendStringTo(String string, String filePath) {
        appendContent(string, filePath);
    }



    private static void writeCollection(Collection<?> collection, String filePath) {
        Path realPath = Config.get().apkOutputDir.resolve(filePath);
        File file = realPath.toFile();

        try {
            FileUtils.writeLines(file, ENCODING, collection, false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void writeContent(String content, String filePath) {
        Path realPath = Config.get().apkOutputDir.resolve(filePath);
        File file = realPath.toFile();

        try {
            FileUtils.writeStringToFile(file, content, ENCODING, false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void appendContent(String content, String filePath) {
        Path realPath = Config.get().apkOutputDir.resolve(filePath);
        File file = realPath.toFile();

        try {
            FileUtils.writeStringToFile(file, content, ENCODING, true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
