package com.mongo.load;

import com.mongo.util.SftpUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/15 14:54
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  spring 定时任务的程序入口
 */
public class App {

    /**
     * 日志
     */
    public Log LOG = LogFactory.getLog(App.class);

    /**
     * 加载配置文件
     */
    public static ClassPathXmlApplicationContext context;

    /**
     * 初始化加载配置文件
     */
    static {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    /**
     * 程序定时启动执行入口
     */
    public void exture() {
        // 默认下载昨天一天的数据
        String yyyyMMdd = new DateTime().minusDays(2).toString("yyyyMMdd");
        String sourcePath = "/opt/software/huiwork/data/" + yyyyMMdd;
        LOG.info("开始导入数据：" + sourcePath);

        // 利用sftp分别初始化服务器1和服务器2并下载数据。
        SftpUtils sftpUtils = new SftpUtils();
        sftpUtils.initSSH1();
        sftpUtils.readFile(sourcePath);
        sftpUtils.initSSH2();
        sftpUtils.readFile(sourcePath);

        //将下载到本地的数据导入到Mongo中。
        LoadDataToMongo loadDataToMongo = (LoadDataToMongo) context.getBean("loadDataToMongo");
        loadDataToMongo.loadData();
    }

    public static void main(String[] args) {
        App app = new App();
        app.exture();
    }
}
