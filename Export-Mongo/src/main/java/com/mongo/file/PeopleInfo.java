package com.mongo.file;

import com.mongo.util.FileAnalysis;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/18 16:11
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   人员属性的一些信息
 */
public class PeopleInfo {

    public String sourcePath = "C:\\Users\\sssd\\Desktop\\横琴数据导出准备\\sourcedata\\people.txt";
    public String savePath = "C:\\Users\\sssd\\Desktop\\横琴数据导出准备\\data\\peopleInfo.csv";

    /**
     * 人口属性的处理
     */
    public void peopleInfoAnalysis() {
        try {
            List<String> lines = FileUtils.readLines(new File(sourcePath), "utf-8");
            int num = 1;
            for (String line : lines) {
                System.out.println("开始处理第" + num++ + "条数据");
                String[] split = line.split("\t");
                if (split.length == 3) {
                    String tempStr = split[2];
                    String matchStr = FileAnalysis.matchStr(tempStr);
                    String saveStr = split[0] + "," + split[1] + "," + matchStr + "\n";
                    FileUtils.writeStringToFile(new File(savePath), saveStr, "gbk", true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PeopleInfo peopleInfo = new PeopleInfo();
        peopleInfo.peopleInfoAnalysis();
    }
}
