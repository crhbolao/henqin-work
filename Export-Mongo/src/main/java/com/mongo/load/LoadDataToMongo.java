package com.mongo.load;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongo.service.SavaDataService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/14 17:45
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   实现将本地文件导入到mongo中。
 */
public class LoadDataToMongo {

    /**
     * 日志
     */
    public Log LOG = LogFactory.getLog(LoadDataToMongo.class);

    /**
     * 将数据保存到 Mongo service
     */
    public SavaDataService savaDataService;

    /**
     * 远程服务器保存到本地的路径
     */
    public String savaPathPre;

    public LoadDataToMongo() {

    }

    /**
     * 将本地文件导入到mongo中
     */
    public void loadData() {
        // 待导入mongo的数据目录
        String yyyyMMdd = new DateTime().minusDays(2).toString("yyyyMMdd");
        String savaDir = savaPathPre.endsWith("/") ? savaPathPre + yyyyMMdd : savaPathPre + "/" + yyyyMMdd;
        File dirFile = new File(savaDir);
        if (dirFile.exists()) {
            File[] files = dirFile.listFiles();
            // 遍历文件并把数据导入到mongo中
            for (File file : files) {
                try {
                    List<String> lines = FileUtils.readLines(file, "UTF-8");
                    for (String line : lines) {
                        JSONObject jsonObject = (JSONObject) JSON.parse(line);
                        savaDataService.saveDataToMongo(jsonObject);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setSavaDataService(SavaDataService savaDataService) {
        this.savaDataService = savaDataService;
    }

    public void setSavaPathPre(String savaPathPre) {
        this.savaPathPre = savaPathPre;
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        LoadDataToMongo loadDataToMongo = (LoadDataToMongo) context.getBean("loadDataToMongo");
        loadDataToMongo.loadData();
    }

}
