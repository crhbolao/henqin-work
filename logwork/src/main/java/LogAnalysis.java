import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/10/8 13:46
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   日志分析，从日志里面加载文件
 */
public class LogAnalysis {

    /**
     * 从日志中提取数据
     */
    public void copyFromLog() {
        try {
            String path = "C:\\Users\\sssd\\Desktop\\横琴二次导出\\detective.10.log";
            String savePath = "C:\\Users\\sssd\\Desktop\\横琴二次导出\\data\\detective-10.txt";
            LineIterator it = FileUtils.lineIterator(new File(path), "UTF-8");
            int i = 1;
            while (it.hasNext()) {
                System.out.println("开始处理第" + i++ + "数据");
                String line = it.nextLine();
                if ((line.contains("04:71:4B:2C:B7:6D")
                        || line.contains("04:71:4B:2C:B7:65")
                        || line.contains("04:71:4B:2C:B7:55")
                        || line.contains("64:05:E9:0D:F0:7D")
                        || line.contains("04:71:4B:2C:B7:3D")
                        || line.contains("04:71:4B:2C:B7:2D")
                        || line.contains("80:81:00:67:E5:81")
                        || line.contains("04:71:4B:2C:A4:85")
                        || line.contains("80:81:00:67:E4:81")
                        || line.contains("84:F3:EB:58:34:CB")
                        || line.contains("84:F3:EB:58:35:0B")
                        || line.contains("DC:4F:22:40:84:95")
                        || line.contains("DC:4F:22:52:A3:82")) && line.contains("probe")) {
                    String[] split = line.split(" ");
                    // 整理读取的数据
                    String timeStr = "2018-" + split[0] + " " + split[1];
                    String[] split1 = line.split(" 探针上传的数据为：");
                    String wifidatas = split1[1];
                    JSONObject tempJson = (JSONObject) JSON.parse(wifidatas);
                    String mac1 = tempJson.getString("mac");
                    JSONArray datas = tempJson.getJSONArray("probe");
                    for (Object data : datas) {
                        JSONObject parse = (JSONObject) JSON.parse(String.valueOf(data));
                        String clientMac = "";
                        if (parse.containsKey("M")) {
                            clientMac = parse.getString("M");
                        } else {
                            clientMac = parse.getString("m");
                        }
                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("site", "201");
//                        jsonObject.put("rssis", "84");
                        jsonObject.put("clientMac", clientMac);
                        jsonObject.put("updateTime", timeStr);
//                        jsonObject.put("isNew", "0");
                        jsonObject.put("wifiLicense", mac1);
//                        jsonObject.put("activity", "1");
                        FileUtils.writeStringToFile(new File(savePath), jsonObject.toJSONString() + "\n", "UTF-8", true);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        LogAnalysis logAnalysis = new LogAnalysis();
        logAnalysis.copyFromLog();
    }


}
