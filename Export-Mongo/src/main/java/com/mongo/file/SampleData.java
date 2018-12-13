package com.mongo.file;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/10/9 11:25
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   对mongo中导出的数据进行抽样统计
 */
public class SampleData {

    /**
     * 待抽样的数据
     */
    public String SourcePath = "C:\\Users\\sssd\\Desktop\\横琴二次导出\\result\\wifiSourceData.csv";

    /**
     * 抽样以后保存的数据
     */
    public String savePath = "C:\\Users\\sssd\\Desktop\\横琴二次导出\\result\\wifiSampleData.csv";

    /**
     * 用来待保存的数据
     */
    public Map<String, LinkedList<String>> finalDatas = new HashMap<String, LinkedList<String>>();

    /**
     * 随机抽样的阈值
     */
    public Integer yuzhi = 2000;

    public SampleData() {

        /**
         * "04714b2cb755"
         * "04714b2cb76d"
         * "04714b2cb73d"
         * "04714b2cb72d"
         * "6405e90df07d"
         * "80810067e481"
         * "04714b2cb765"
         * "04714b2ca485"
         * "80810067e581"
         * "84f3eb5834cb"
         * "84f3eb58350b"
         * "dc4f22408495"
         * "dc4f2252a382"
         */

        LinkedList<String> list1 = new LinkedList<String>();
        finalDatas.put("04714b2cb755", list1);
        LinkedList<String> list2 = new LinkedList<String>();
        finalDatas.put("04714b2cb76d", list2);
        LinkedList<String> list3 = new LinkedList<String>();
        finalDatas.put("04714b2cb73d", list3);
        LinkedList<String> list4 = new LinkedList<String>();
        finalDatas.put("04714b2cb72d", list4);
        LinkedList<String> list5 = new LinkedList<String>();
        finalDatas.put("6405e90df07d", list5);
        LinkedList<String> list6 = new LinkedList<String>();
        finalDatas.put("80810067e481", list6);
        LinkedList<String> list7 = new LinkedList<String>();
        finalDatas.put("04714b2cb765", list7);
        LinkedList<String> list8 = new LinkedList<String>();
        finalDatas.put("04714b2ca485", list8);
        LinkedList<String> list9 = new LinkedList<String>();
        finalDatas.put("80810067e581", list9);
        LinkedList<String> list10 = new LinkedList<String>();
        finalDatas.put("84f3eb5834cb", list10);
        LinkedList<String> list11 = new LinkedList<String>();
        finalDatas.put("84f3eb58350b", list11);
        LinkedList<String> list12 = new LinkedList<String>();
        finalDatas.put("dc4f22408495", list12);
        LinkedList<String> list13 = new LinkedList<String>();
        finalDatas.put("dc4f2252a382", list13);
    }

    /**
     * 对数据进行抽样
     */
    public void dataSample() {
        try {
            LineIterator it = FileUtils.lineIterator(new File(SourcePath), "UTF-8");
            int linenum = 1;
            while (it.hasNext()) {
                System.out.println("开始处理的第" + linenum++ + "行数据。");
                String next = it.next();
                String[] split = next.split(",");
                String firstTime = split[2];
                String secondTime = split[3];
                int i = computeTimeError(firstTime, secondTime);
                if (i > 0 && i < 10) {
                    String wifiMac = split[0];
                    if (finalDatas.containsKey(wifiMac)){
                        LinkedList<String> list = finalDatas.get(wifiMac);
                        list.add(next);
                        finalDatas.put(wifiMac, list);
                    }
                }
            }
            savaDatas(finalDatas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 用来保存数据
     *
     * @param map 待保存的数据
     */
    public void savaDatas(Map<String, LinkedList<String>> map) throws Exception {
        for (Map.Entry<String, LinkedList<String>> entry : map.entrySet()) {
            LinkedList<String> linkedList = entry.getValue();
            if (linkedList.size() >= yuzhi) {
                Collections.shuffle(linkedList);
                List<String> subList = linkedList.subList(0, yuzhi - 1);
                for (String s : subList) {
                    FileUtils.writeStringToFile(new File(savePath), s + "\n", "gbk", true);
                }
            } else {
                for (String s : linkedList) {
                    FileUtils.writeStringToFile(new File(savePath), s + "\n", "gbk", true);
                }
            }
        }
    }

    /**
     * 用来计算时间差
     *
     * @param firstTime
     * @param secondTime
     * @return
     */
    public int computeTimeError(String firstTime, String secondTime) {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
        DateTime startTime = DateTime.parse(firstTime, format);
        DateTime endTime = DateTime.parse(secondTime, format);
        Period p = new Period(startTime, endTime, PeriodType.days());
        int days = p.getDays();
        return days;
    }


    public static void main(String[] args) {
        SampleData sampleData = new SampleData();
        sampleData.dataSample();
    }

}
