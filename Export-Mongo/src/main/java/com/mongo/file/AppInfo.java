package com.mongo.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongo.load.App;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/18 18:18
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   应用信息的统计
 */
public class AppInfo {

    public String columnPath = "C:\\Users\\sssd\\Desktop\\横琴数据导出准备\\sourcedata\\app列名.txt";
    public String sourcePath = "C:\\Users\\sssd\\Desktop\\横琴数据导出准备\\sourcedata\\app.txt";
    public LinkedList<String> columnList = new LinkedList<String>();
    public String savaPath = "C:\\Users\\sssd\\Desktop\\横琴数据导出准备\\data\\appInfo.csv";

    /**
     * 初始化
     */
    public AppInfo() {
        try {
            List<String> lines = FileUtils.readLines(new File(columnPath), "UTF-8");
            for (String line : lines) {
                columnList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 用来进行数据分析并保存
     */
    public void appAnalysis() {
        try {
            List<String> lines = FileUtils.readLines(new File(sourcePath), "UTF-8");
            String label = "手机mac,app应用信息," + StringUtils.join(columnList, ",") + "\n";
            FileUtils.writeStringToFile(new File(savaPath), label, "gbk", true);
            int lineNum = 1;
            for (String line : lines) {
                System.out.println("开始处理第" + lineNum++ + "行数据！");
                int[] indexs = new int[columnList.size()];
                String[] lineSplit = line.split("\t");
                if (lineSplit.length == 2) {
                    String clientMac = lineSplit[0];
                    String appInfo = lineSplit[1];
                    String[] appInfos = appInfo.split("\\|");
                    for (String info : appInfos) {
                        String name = info.split(":")[0];
                        Integer index = columnList.indexOf(name);
                        indexs[index] = 1;
                        lableCount(index, indexs);
                    }
                    String savaStr = clientMac + "," + appInfo + "," + StringUtils.join(ArrayUtils.toObject(indexs), ",") + "\n";
                    FileUtils.writeStringToFile(new File(savaPath), savaStr, "gbk", true);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 标签汇总
     */
    public int[] lableCount(int index, int[] indexs) {
        if (0 < index && index < 8) {
            indexs[0] = 1;
            return indexs;
        } else if (8 < index && index < 21) {
            indexs[8] = 1;
            return indexs;
        } else if (21 < index && index < 34) {
            indexs[21] = 1;
            return indexs;
        } else if (34 < index && index < 43) {
            indexs[34] = 1;
            return indexs;
        } else if (43 < index && index < 50) {
            indexs[43] = 1;
            return indexs;
        } else if (50 < index && index < 56) {
            indexs[50] = 1;
            return indexs;
        } else if (56 < index && index < 63) {
            indexs[56] = 1;
            return indexs;
        } else if (63 < index && index < 78) {
            indexs[63] = 1;
            return indexs;
        } else if (78 < index && index < 87) {
            indexs[78] = 1;
            return indexs;
        } else if (87 < index && index < 95) {
            indexs[87] = 1;
            return indexs;
        } else if (95 < index && index < 116) {
            indexs[95] = 1;
            return indexs;
        } else if (116 < index && index < 128) {
            indexs[116] = 1;
            return indexs;
        } else if (128 < index && index < 140) {
            indexs[128] = 1;
            return indexs;
        } else if (140 < index && index < 156) {
            indexs[140] = 1;
            return indexs;
        } else if (156 < index && index < 162) {
            indexs[156] = 1;
            return indexs;
        } else if (162 < index && index < 167) {
            indexs[162] = 1;
            return indexs;
        } else if (167 < index && index < 172) {
            indexs[167] = 1;
            return indexs;
        } else if (172 < index && index < 176) {
            indexs[172] = 1;
            return indexs;
        } else {
            indexs[176] = 1;
            return indexs;
        }
    }

    public static void main(String[] args) {
        AppInfo appInfo = new AppInfo();
        appInfo.appAnalysis();
    }
}
