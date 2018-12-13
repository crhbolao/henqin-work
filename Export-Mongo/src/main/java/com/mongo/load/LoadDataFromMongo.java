package com.mongo.load;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongo.util.DateUtils;
import com.mongo.util.ListSort;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.WildcardType;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/15 16:54
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   从 Mongo 中导出数据
 */
public class LoadDataFromMongo {

    /**
     * 日志文件
     */
    public Log LOG = LogFactory.getLog(LoadDataFromMongo.class);

    /**
     * mongo Template
     */
    public MongoTemplate mongoTemplate;

    /**
     * 保存探针原始数据的文件路径
     */
    public File wifiSourceDataFile = new File("C:\\Users\\sssd\\Desktop\\华侨数据导出\\data\\wifiSourceData.csv");

    /**
     * 保存探针按天统计的结果
     */
    public File wifiDayCountFile = new File("C:\\Users\\sssd\\Desktop\\华侨数据导出\\data\\wifiDayCountFile.csv");


    public List<String> WIFIMACS = new ArrayList<String>();

    /**
     * 初始化需要过滤的探针mac
     */
    public void init(){
/*        WIFIMACS.add("04714b2cb755");
        WIFIMACS.add("04714b2cb76d");
        WIFIMACS.add("04714b2cb73d");
        WIFIMACS.add("04714b2cb72d");
        WIFIMACS.add("6405e90df07d");
        WIFIMACS.add("80810067e481");
        WIFIMACS.add("04714b2cb765");
        WIFIMACS.add("04714b2ca485");
        WIFIMACS.add("80810067e581");*/

        WIFIMACS.add("c8eea6385fe6");
        WIFIMACS.add("c8eea63860de");
        WIFIMACS.add("c8eea6386196");
        WIFIMACS.add("c8eea6386232");
    }


    /**
     * 从Mongo中读取数据
     */
    public void readDataFromMongo() {

        init();
        // mongo 查询的时间段
        String startTime = new DateTime().minusDays(30).toString("yyyy-MM-dd HH:mm:ss");
        String endTime = new DateTime().toString("yyyy-MM-dd HH:mm:ss");
        System.out.println("开始时间：" + startTime + "结束时间：" + endTime);
        DBObject query = new BasicDBObject();

        // 指定主键查询
//        ArrayList<String> list = new ArrayList<String>();
//        list.add("1801f11581ba");
//        query.put("_id", new BasicDBObject("$in", list));

//        // Mongo实现查询
//        query.put("lastUpdate", (new BasicDBObject("$gte", startTime)).append("$lte", endTime));
//        // 网上说使用这种查询方法速度快。
//        DBCursor dbCursor = mongoTemplate.getCollection("WiFiMac").find(query);

        // 不限制条件，查找mongo中所有的数据。
        DBCursor dbCursor = mongoTemplate.getCollection("WiFiMac").find();
        int num = 1;
        while (dbCursor.hasNext()) {
            System.out.println("开始处理数据----" + num++);
            DBObject object = dbCursor.next();
            JSONObject jsonObject = (JSONObject) JSON.parse(object.toString());
            // 用来统计探针第一次监测时间和最后一次时间，以及对应的每天的处理（按景点分割）
            JSONArray wifimacs = jsonObject.getJSONArray("wifimacs");
            String clientMac = jsonObject.getString("clientMac");
            for (Object tempWifi : wifimacs) {
                // tocode 可以在这里添加探针mac的过滤

                String wifimac = (String) tempWifi;
                if (!WIFIMACS.contains(wifimac.toLowerCase())){
                    continue;
                }

                // 获取数据的时间{wifimac, update}
                JSONArray datas = jsonObject.getJSONArray("data");
                // 用来存放每个手机mac的更新时间
                HashSet<Long> timeSet = new HashSet<Long>();
                // 用来按天数统计
                HashMap<String, Integer> daysMap = new HashMap<String, Integer>();
                // 对其中的更新数据datas，进行遍历。
                for (Object data : datas) {
                    JSONObject parse = (JSONObject) JSON.parse(String.valueOf(data));
                    // 按其中的wifimac进行统计
                    if (StringUtils.equalsIgnoreCase(parse.getString("wifiMac"), wifimac)) {
                        // 存放更新时间
                        long updatedAt = parse.getLongValue("updatedAt");
                        timeSet.add(updatedAt);
                        // 按天统计
                        String dayTime = DateUtils.longTimeToDateString(updatedAt);
                        if (daysMap.containsKey(dayTime)) {
                            daysMap.put(dayTime, daysMap.get(dayTime) + 1);
                        } else {
                            daysMap.put(dayTime, 1);
                        }
                    }
                }

                // 用来保存 wifiSourceData （第一次监测和最后一次监测的时间）
                StringBuffer stringBuffer = new StringBuffer();
                // 将set类型的时间转换为list时间
                ArrayList<Long> timeList = new ArrayList<Long>(timeSet);
                // 对list进行排序
                ListSort.sort(timeList, 0, timeList.size() - 1);
                // 分别获取开始时间和结束时间，并保存结果
                String timeStart = DateUtils.longTimeToDateTimeString(timeList.get(0));
                String timeEnd = DateUtils.longTimeToDateTimeString(timeList.get(timeList.size() - 1));
                stringBuffer.append(tempWifi + ",").append(clientMac + ",").append(timeStart + ",").append(timeEnd + "\n");
                try {
                    FileUtils.writeStringToFile(wifiSourceDataFile, stringBuffer.toString(), "gbk", true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 用来保存统计的天数，以及对应的每天出现的次数
                StringBuffer dayResBuf = new StringBuffer();
                int size = daysMap.entrySet().size();
                dayResBuf.append(wifimac + ",").append(clientMac + ",").append(size + ",");
                for (Map.Entry<String, Integer> entry : daysMap.entrySet()) {
                    dayResBuf.append(entry.getKey() + ":" + entry.getValue() + ",");
                }
                dayResBuf.deleteCharAt(dayResBuf.length() - 1).append("\n");
                try {
                    FileUtils.writeStringToFile(wifiDayCountFile, dayResBuf.toString(), "gbk", true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        LoadDataFromMongo loadDataFromMongo = (LoadDataFromMongo) context.getBean("loadDataFromMongo");
        loadDataFromMongo.readDataFromMongo();
    }
}
