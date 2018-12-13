import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/6/29 9:20
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   模拟发送 wifi 数据
 */
public class WifiMacToHttp {

    private HttpClient client = HttpClientBuilder.create().build();

    public WifiMacToHttp() {
    }

    //探针Mac地址数据
    private static String[] wifiMacs = {"14E6E4E327FE", "14E6E4E34AD8"};

    //移动设备Mac地址
    private static String[] clientMacs = {"b8fc9a0031e8", "c83a35cc0637", "7802f82a152d", "b8fc9a000b51", "b8fc9a00678e",
            "b8fc9a000bf8", "3469872386d2", "28faa0bc828f", "28e34764d680", "c83a35c118a1",
            "94659c47b396", "485ab647eced", "404d7fbe1431", "b40b44010afb", "daa119a2882d",
            "daa11912f9f6", "34de1ac8154f", "88447728c717", "3423baa82c00", "f04347213cea",
            "74a52847983c", "c48e8f748a5b", "b0e235c7711b", "4851b7f56216", "005a1329255f",
            "daa11967d262", "6c40087567f3", "e82aeac19fcb", "4eedff201a91", "881fa12294b2",
            "7c0191314013", "009acdbf3de7", "fa0316c56ade", "584498544f42", "0c1dafd89f83",
            "a81b5a95b836", "acc1ee3efe61", "f0c850e6db2f", "daa119d3153e", "7c04d07412c2",
            "a25d2d382b4d", "102ab3d1a638", "1ccde576c561", "82b1110399b4", "caded0b7205f",
            "c83a35c54daf", "c8f230925aa3", "0e40163dc1b2", "0024d6c0e1ad", "daa119d200ff"};

    /**
     * 封装数据
     *
     * @return
     */
    public static List<JSONObject> createJSONData2() {

        ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();

        for (String wifimac : wifiMacs) {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mac", wifimac);


            JSONArray jsonArray = new JSONArray();
            for (String s : clientMacs) {
                JSONObject tmpJsonObject = new JSONObject();
                tmpJsonObject.put("M", s);
                tmpJsonObject.put("P", -80);
                jsonArray.add(tmpJsonObject);
            }

            jsonObject.put("probe", jsonArray);
            jsonObjects.add(jsonObject);
        }

        return jsonObjects;
    }

    /**
     * 探针上传的数据格式为:
     * {"mac":"20:5C:FA:7E:29:57",
     * "probe":[
     * {"M":"7c04d07412c2","P":-79},
     * {"M":"b8fc9a0031e8","P":-76},
     * {"M":"b8fc9a000bf8","P":-79},
     * {"M":"c83a35c20904","P":-78},
     * {"M":"b8fc9a00678e","P":-79}
     * ]}
     */

    @Test
    public void testSendWifiMacJSON() throws Exception {
        List<JSONObject> datas = createJSONData2();
        int i = 0;
        for (JSONObject jsonData : datas) {
            if (i == 5) {
                break;
            }
            System.out.println("发送的json请求数据为：" + jsonData.toJSONString());
            StringEntity entity = new StringEntity(jsonData.toJSONString());
//            HttpPost post = new HttpPost("http://192.168.1.239:8020/detective/wifi/wifiJSON1");
            HttpPost post = new HttpPost("http://114.116.31.82:8080/wifi/wifiJSON1");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                System.out.println(IOUtils.toString(response.getEntity().getContent()));
                System.out.println(response.getLastHeader("Content-Type"));
            } else {
                System.out.println("此次响应失败。。。");
            }
            i++;
        }
    }

    @Test
    public void testPath() throws Exception{
        String path = "C:\\Users\\sssd\\Desktop\\clientMacData\\2018-06-28";
        File file = new File(path);
        System.out.println(file.getName());
    }

}
