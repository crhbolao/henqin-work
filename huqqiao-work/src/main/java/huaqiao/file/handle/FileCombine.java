package huaqiao.file.handle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/12/7 9:20
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  华侨文件的合并
 */
public class FileCombine {

    /**
     * 输入文件的目录路径
     */
    public String inoutDir = "C:\\Users\\sssd\\Desktop\\华侨数据\\wifidata";

    /**
     * 所有合并的数据
     */
    public String savePath = "C:\\Users\\sssd\\Desktop\\华侨数据\\wifidata\\alldata.txt";

    /**
     * 日志文件
     */
    public Log LOG = LogFactory.getLog(FileCombine.class);

    /**
     * 用来合并文件
     *
     * @throws Exception
     */
    public void combine() throws Exception {
        File fileDir = new File(inoutDir);
        File[] files = fileDir.listFiles();
        for (File file : files) {
            LOG.info("开始处理文件：" + file.getName());
            StringBuffer sb = new StringBuffer();
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            for (String line : lines) {
                String[] split = line.split(",");
                String tempLine = split[0] + "," + split[1] + "," + split[2];
                sb.append(tempLine).append("\n");
            }
            FileUtils.writeStringToFile(new File(savePath), sb.toString(), "UTF-8", true);
        }
    }


    public static void main(String[] args) throws Exception {
        FileCombine fileCombine = new FileCombine();
        fileCombine.combine();
    }

}
