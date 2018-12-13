package temp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/14 17:50
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class FtpUtils {

    public static Log LOG = LogFactory.getLog(FtpUtils.class);

    public static FTPClient ftpClient = new FTPClient();

    public static String url;
    public static Integer port;
    public static String userName;
    public static String passWord;
    public static Properties prop = new Properties();

    static {
        try {
            // linux下自定义jar包的加载
//            String property = System.getProperty("user.dir");
//            System.out.println(property);
//            prop.loadFromXML(new FileInputStream(property + "/conf/sql.xml"));

            ClassLoader classLoader = FtpUtils.class.getClassLoader();
            if (classLoader == null) {
                classLoader = Thread.currentThread().getContextClassLoader();
            }
            InputStream inputStream = classLoader.getResourceAsStream("conf.properties");
            prop.load(inputStream);
            url = prop.getProperty("url");
            port = Integer.valueOf(prop.getProperty("port"));
            userName = prop.getProperty("userName");
            passWord = prop.getProperty("passWord");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化连接
     *
     * @return
     */
    public static void initClient() {
        try {
            ftpClient.connect(url, port);
            ftpClient.login(userName, passWord);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFileFromFtp(String dirPath) {
        initClient();
        try {
            ftpClient.changeWorkingDirectory(dirPath);
            FTPFile[] ftpFiles = ftpClient.listDirectories();
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.isDirectory()) {
                    String dirName = ftpFile.getName();
                    String tempPath = dirPath.endsWith("/") ? dirPath + dirName : dirPath + "/" + dirName;
                    readFileFromFtp(tempPath);
                } else if (ftpFile.isFile()) {
                    String localPath = "C:\\Users\\sssd\\Desktop\\本地文件.txt";
                    FileOutputStream fileOutputStream = new FileOutputStream(localPath);
                    String fileName = ftpFile.getName();
                    ftpClient.retrieveFile(fileName, fileOutputStream);
                    fileOutputStream.close();
//                    ftpClient.deleteFile(fileName);
                } else {
                    LOG.info("未知的文件格式！！！");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        FtpUtils ftpUtils = new FtpUtils();
        String path = "/opt/software/huiwork/data/20180914";
        ftpUtils.readFileFromFtp(path);
    }

}
