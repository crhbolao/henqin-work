package com.mongo.util;

import java.io.*;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp.LsEntry;


/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/15 10:44
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:     Java sftp实现将远程服务器的文件下载到本地
 */
public class SftpUtils {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(SftpUtils.class);

    /**
     * 服务器 url
     */
    public static String url;

    /**
     * 服务器 port
     */
    public static Integer port;

    /**
     * 登陆用户名
     */
    public static String userName;

    /**
     * 登陆密码
     */
    public static String passWord;

    /**
     * 本地保存文件路径前缀
     */
    public static String savePathPre;

    /**
     * 加载的配置文件
     */
    public static Properties prop = new Properties();

    /**
     * 连接 channel
     */
    public static Channel channel = null;

    /**
     * 会话sshSession
     */
    public static Session sshSession = null;

    /**
     * 初始化服务1的配置
     */
    public static void initSSH1() {
        try {
            // linux下自定义配置文件的加载
            String property = System.getProperty("user.dir");
            System.out.println(property);
            prop.load(new FileInputStream(property + "/conf/conf.properties"));

            // 本地idea加载配置文件
//            ClassLoader classLoader = FtpUtils.class.getClassLoader();
//            if (classLoader == null) {
//                classLoader = Thread.currentThread().getContextClassLoader();
//            }
//            InputStream inputStream = classLoader.getResourceAsStream("conf.properties");
//            prop.load(inputStream);

            url = prop.getProperty("ssh1-url");
            port = Integer.valueOf(prop.getProperty("ssh1-port"));
            userName = prop.getProperty("ssh1-userName");
            passWord = prop.getProperty("ssh1-passWord");
            savePathPre = prop.getProperty("savePathPre");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化服务2的配置
     */
    public void initSSH2() {
        try {
            // linux下自定义jar包的加载
            String property = System.getProperty("user.dir");
            System.out.println(property);
            prop.load(new FileInputStream(property + "/conf/conf.properties"));

            // 本地idea加载配置文件
//            ClassLoader classLoader = FtpUtils.class.getClassLoader();
//            if (classLoader == null) {
//                classLoader = Thread.currentThread().getContextClassLoader();
//            }
//            InputStream inputStream = classLoader.getResourceAsStream("conf.properties");
//            prop.load(inputStream);

            url = prop.getProperty("ssh2-url");
            port = Integer.valueOf(prop.getProperty("ssh2-port"));
            userName = prop.getProperty("ssh2-userName");
            passWord = prop.getProperty("ssh2-passWord");
            savePathPre = prop.getProperty("savePathPre");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化连接
     */
    public static void initClient() {
        try {
            JSch jsch = new JSch();
            sshSession = jsch.getSession(userName, url, port);
            sshSession.setPassword(passWord);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            LOG.info("sshsession 初始化成功！！！");
            channel = sshSession.openChannel("sftp");
            channel.connect(1500);
            LOG.info("channel 初始化成功！！！");
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取指定位置的文件
     *
     * @param dirPath 待读取文件目录的地址
     */
    public void readFile(String dirPath) {
        initClient();
        ChannelSftp sftp = null;
        if (StringUtils.isNotBlank(dirPath)) {
            try {
                sftp = (ChannelSftp) channel;
                sftp.cd(dirPath);
                Vector<?> vector = sftp.ls(dirPath);
                for (Object item : vector) {
                    LsEntry entry = (LsEntry) item;
                    String filename = entry.getFilename();
                    if (!StringUtils.equalsIgnoreCase(".", filename) && !StringUtils.equalsIgnoreCase("..", filename)) {
                        int index = dirPath.lastIndexOf("/");
                        // 获取目录名称（以及数据保存的时间）
                        String dirName = dirPath.substring(index + 1, dirPath.length());
                        // 拼接本地保存的目录名
                        String saveDirpath = savePathPre.endsWith("/") ? savePathPre + dirName : savePathPre + "/" + dirName;
                        // 如果目录不存在则新建
                        File dirFile = new File(saveDirpath);
                        if (!dirFile.exists()) {
                            dirFile.mkdir();
                        }
                        // 拼接保存到本地的文件
                        String savaPath = saveDirpath.endsWith("/") ? saveDirpath + filename : saveDirpath + "/" + filename;
                        File file = new File(savaPath);
                        // sftp 下载文件到本地
                        sftp.get(filename, new FileOutputStream(file));
                        LOG.info("文件" + filename + "下载成功！");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeChannel(sftp);
                closeChannel(channel);
                closeSession(sshSession);
            }
        }

    }

    private static void closeChannel(Channel channel) {
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        }
    }

    private static void closeSession(Session session) {
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        SftpUtils sftpUtils = new SftpUtils();
        sftpUtils.initSSH2();
        sftpUtils.readFile("/opt/software/huiwork/data/20180914");
    }
}
