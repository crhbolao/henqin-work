package com.mongo.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/18 16:37
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    地点属性的统计
 */
public class LocationInfo {

    public String sourcePath = "C:\\Users\\sssd\\Desktop\\横琴数据导出准备\\sourcedata\\location.txt";
    public String saveParh = "C:\\Users\\sssd\\Desktop\\横琴数据导出准备\\data\\locationInfo.csv";


    public void locationAnalysis() {
        try {
            // 统计数据
            List<String> lines = FileUtils.readLines(new File(sourcePath), "UTF-8");
            JSONObject allJson = new JSONObject();
            int numLine = 1;
            for (String line : lines) {
                System.out.println("开始处理第" + numLine++ + "条数据");
                String[] split = line.split("\t");
                if (split.length == 2) {
                    String sceneName = split[0];
                    String tempLocation = split[1];
                    String[] locations = tempLocation.split("\\|");
                    JSONObject locationJson = null;
                    if (allJson.containsKey(sceneName)) {
                        String string = allJson.getString(sceneName);
                        try {
                            locationJson = (JSONObject) JSON.parse(string);
                            for (String location : locations) {
                                if (locationJson.containsKey(location)) {
                                    Integer num = locationJson.getInteger(location);
                                    locationJson.put(location, num + 1);
                                } else {
                                    locationJson.put(location, 1);
                                }
                            }
                        } catch (Exception e) {
                            locationJson = new JSONObject();
                            for (String location : locations) {
                                locationJson.put(location, 1);
                            }
                        }
                    } else {
                        locationJson = new JSONObject();
                        for (String location : locations) {
                            locationJson.put(location, 1);
                        }
                    }
                    allJson.put(sceneName, locationJson);
                }
            }

            // 保存数据
            for (Map.Entry<String, Object> entry : allJson.entrySet()) {
                String sceneName = entry.getKey();
                String value = String.valueOf(entry.getValue());
                JSONObject jsonObject = (JSONObject) JSON.parse(value);
                for (Map.Entry<String, Object> objectEntry : jsonObject.entrySet()) {
                    String savaStr = sceneName + "," + objectEntry.getKey() + "," + String.valueOf(objectEntry.getValue()) + "\n";
                    FileUtils.writeStringToFile(new File(saveParh), savaStr, "gbk", true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.locationAnalysis();
    }

}
