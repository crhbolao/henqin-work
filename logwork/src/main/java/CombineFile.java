import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/11/23 9:49
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   用来将多个文件合并在一起
 */
public class CombineFile {

    public String inputPath= "C:\\Users\\sssd\\Desktop\\横琴二次导出\\data2";
    public String combinePath= "C:\\Users\\sssd\\Desktop\\横琴二次导出\\combine.json";

    public void combine() throws Exception{
        File file = new File(inputPath);
        File[] files = file.listFiles();
        for (File tempFile : files) {
            List<String> lines = FileUtils.readLines(tempFile, "UTF-8");
            StringBuffer sb = new StringBuffer();
            for (String line : lines) {
                sb.append(line).append("\n");
            }
            FileUtils.writeStringToFile(new File(combinePath), sb.toString(), "UTF-8", true);
        }
    }

    public static void main(String[] args)  throws Exception{
        CombineFile combineFile = new CombineFile();
        combineFile.combine();
    }
}
