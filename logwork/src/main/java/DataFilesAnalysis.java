import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/10/12 13:50
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  对数据文件进行分析
 */
public class DataFilesAnalysis {

    public String sourcePath = "C:\\Users\\sssd\\Desktop\\横琴数据导出准备\\loadtomongo\\coffeStreetData.txt";
    public String savePath = "C:\\Users\\sssd\\Desktop\\横琴数据导出准备\\loadtomongo\\coffeStreetData1.txt";
    public List macs;

    public DataFilesAnalysis() {
        macs = new ArrayList();
        macs.add("6405e90e24e5");
//        macs.add("6405e90e24d5");
    }

    /**
     * 用来过滤数据
     */
    public void FilterData() {
        try {
            LineIterator iterator = FileUtils.lineIterator(new File(sourcePath), "UTF-8");
            int lineNum = 1;
            HashMap<String, String> resMap = new HashMap<String, String>();
            while (iterator.hasNext()) {
                System.out.println("开始处理第" + lineNum++ + "行数据");
                String next = iterator.next();
                JSONObject parse = (JSONObject) JSON.parse(next);
                String wifiLicense = parse.getString("wifiLicense");
                String clientMac = parse.getString("clientMac");
                if (wifiLicense != null && macs.contains(wifiLicense)) {
                    resMap.put(clientMac, parse.toJSONString());
                }
            }

            int i = 1;
            for (Map.Entry<String, String> entry : resMap.entrySet()) {
                System.out.println("开始保存第" + i++ +"数据");
                FileUtils.writeStringToFile(new File(savePath), entry.getValue() + "\n", "UTF-8", true);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DataFilesAnalysis dataFilesAnalysis = new DataFilesAnalysis();
        dataFilesAnalysis.FilterData();
    }

}
