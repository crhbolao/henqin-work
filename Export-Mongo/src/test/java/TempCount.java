import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/10/11 14:54
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class TempCount {

    public HashMap<String, Set<String>> map = new HashMap<String, Set<String>>();
    public String mac = "04714b2cb76d";

    public TempCount() {
        HashSet<String> set = new HashSet<String>();
        map.put(mac, set);
    }

    public void readFile() {
        String path = "C:\\Users\\sssd\\Desktop\\temp.json";
        try {
            LineIterator iterator = FileUtils.lineIterator(new File(path), "UTF-8");
            int numLine = 1;
            while (iterator.hasNext()) {
                System.out.println("开始处理第" + numLine++ + "行数据");
                String next = iterator.next();
                JSONObject parse = (JSONObject) JSON.parse(next);
                String wifiLicense = parse.getString("wifiLicense");
                if (StringUtils.equalsIgnoreCase(wifiLicense, mac)) {
                    String clientMac = parse.getString("clientMac");
                    Set<String> set = map.get(mac);
                    set.add(clientMac);
                }
            }
            System.out.println("统计出的结果大小为;" + map.get(mac).size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TempCount tempCount = new TempCount();
        tempCount.readFile();
    }

}
