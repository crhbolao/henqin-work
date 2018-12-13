package com.mongo.file;

import com.mongo.util.HttpUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/19 13:58
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    调用TD接口的相关统计
 */
public class TDInfo {

    public String sourcePath = "C:\\Users\\sssd\\Desktop\\华侨数据导出\\data\\wifiSampleData.csv";
    public String savaPath = "C:\\Users\\sssd\\Desktop\\华侨数据导出\\data\\wifiAllInfoData.csv";


    public void TdAnalysis() {
        try {
            List<String> lines = FileUtils.readLines(new File(sourcePath), "UTF-8");
            int num = 1;
            for (String line : lines) {
                System.out.printf("第%d条正在调用TD接口%n", num++);
                String[] lineSplit = line.split(",");
                String clientMac = lineSplit[1];
                final String join = (String) HttpUtils.requestGet("http://113.200.115.89:8082/td/alltag?mac=" + clientMac.replaceAll(":", ""));
                if (StringUtils.isNotBlank(join)) {
                    final String res = line + "," + join + "\n";
                    FileUtils.writeStringToFile(new File(savaPath), res, "gbk", true);
                } else {
                    final String res = line + "," + ",,,,,," + "\n";
                    FileUtils.writeStringToFile(new File(savaPath), res, "gbk", true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TDInfo tdInfo = new TDInfo();
        tdInfo.TdAnalysis();
    }
}
