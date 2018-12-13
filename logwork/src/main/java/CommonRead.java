import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/10/24 18:30
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class CommonRead {

    public static void main(String[] args) throws Exception {
        List<String> lines = FileUtils.readLines(new File("C:\\Users\\sssd\\Desktop\\s04_07.txt"), "UTF-8");
        HashSet<String> set = new HashSet<String>();
        for (String line : lines) {
            JSONObject parse = (JSONObject) JSON.parse(line);
            set.add(parse.getString("clientMac"));
        }
        System.out.println("统计的结果为：" + set.size());
    }

}
