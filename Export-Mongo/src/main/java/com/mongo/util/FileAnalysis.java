package com.mongo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/18 15:48
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   其中对文件内容进行处理 ----- 正则匹配
 */
public class FileAnalysis {

    public static Pattern p = Pattern.compile("(?:(已婚))|(?:(家有宝宝|准爸准妈))|(?:(有车))|(?:(男|女))|(\\d+\\-\\d+岁)");

    public static String matchStr(String str){
        Matcher m = p.matcher(str);
        StringBuffer sbr1 = new StringBuffer();
        StringBuffer sbr2 = new StringBuffer();
        StringBuffer sbr3 = new StringBuffer();
        StringBuffer sbr4 = new StringBuffer();
        StringBuffer sbr5 = new StringBuffer();
        while (m.find()) {
            sbr1.append(m.group(1) == null ? "" : m.group(1));
            sbr2.append(m.group(2) == null ? "" : m.group(2));
            sbr3.append(m.group(3) == null ? "" : m.group(3));
            sbr4.append(m.group(4) == null ? "" : m.group(4));
            sbr5.append(m.group(5) == null ? "" : m.group(5));
        }
        StringBuffer res = new StringBuffer();
        res.append(sbr1.toString().equals("") ? "未婚" : sbr1).append(",")
                .append(sbr2.toString().equals("") ? "无宝宝": sbr2 ).append(",")
                .append(sbr3.toString().equals("") ? "无车": sbr3 ).append(",")
                .append(sbr4 + ",")
                .append(sbr5.toString().equals("") ? "0": sbr5);
        return res.toString();
    }

    public static void main(String[] args) {
        String str = "已婚:0.7|家有宝宝:0.7|有车:100.0|15-25岁:100.0|男:83.0";
        String s = FileAnalysis.matchStr(str);
        System.out.println(s);
    }

}
