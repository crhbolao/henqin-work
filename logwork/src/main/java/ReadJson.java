import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/11/7 11:47
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class ReadJson {

    public static void main(String[] args) throws Exception {
        List<String> lines = FileUtils.readLines(new File("C:\\Users\\sssd\\Desktop\\华侨数据导出\\data\\source.txt"), "UTF-8");
        String savePath = "C:\\Users\\sssd\\Desktop\\华侨数据导出\\data\\wifiSampleData.csv";
        for (String line : lines) {
            JSONObject parse = (JSONObject) JSON.parse(line);
            String tempLine = parse.getString("wifiLicense") + "," + parse.getString("clientMac") + "," + parse.getString("updateTime");
            FileUtils.writeStringToFile(new File(savePath), tempLine + "\n", "gbk", true);
        }
    }


}
