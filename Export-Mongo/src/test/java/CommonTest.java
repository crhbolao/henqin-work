import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/15 15:35
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class CommonTest {

    @Test
    public void test() {
        String str = "家有小宝宝:0.7|准爸准妈:0.7|有车:100.0|15-25岁:100.0|男:83.0";
        Pattern p = Pattern.compile("(?:(已婚))|(?:(家有宝宝|准爸准妈))|(?:(有车))|(?:(男|女))|(\\d+\\-\\d+岁)");
        Matcher m = p.matcher(str);
        LinkedList<String> list = new LinkedList<String>();
        StringBuffer sbr1 = new StringBuffer();
        StringBuffer sbr2 = new StringBuffer();
        StringBuffer sbr3 = new StringBuffer();
        StringBuffer sbr4 = new StringBuffer();
        StringBuffer sbr5 = new StringBuffer();
        while (m.find()) {
            sbr1.append(m.group(1) == null ? "" : m.group(1));
            sbr2.append(m.group(2) == null ? "" : m.group(2));
            sbr3.append(m.group(3) == null ? "" : m.group(3));
            sbr4.append(m.group(4) == null ? "" : m.group(4));
            sbr5.append(m.group(5) == null ? "" : m.group(5));
        }
        System.out.println(sbr1.toString().equals("") ? "未婚" : sbr1.toString().equals(""));
        System.out.println(sbr2.toString());
        System.out.println(sbr3.toString());
        System.out.println(sbr4.toString());
        System.out.println(sbr5.toString());
    }

    @Test
    public void test5() {
        String str = "家有小宝宝:0.7|准爸准妈:0.7|已婚:0.7|有车:100.0|15-25岁:100.0|男:83.0";
        Pattern p = Pattern.compile("([已婚]{1,})(?:(家有宝宝|准爸准妈))");
        Matcher m = p.matcher(str);
        List<String> ls = new ArrayList();
        while (m.find()) {
//            ls.add(m.group(1));
            for (int j = 0; j <= m.groupCount(); j++)
                System.out.println("group(" + j + ")[" + m.group(j) + "]");
        }
    }


    @Test
    public void test4() {
        String str = "Twas33332;./';;";
        //([A-Za-z]{1,})匹配第一组的字母，(\d{1,})匹配第二组的数字字符，(\W+)匹配第三组的非字母数字字符
        Matcher m = Pattern.compile("([A-Za-z]{1,})(\\d{1,})(\\W+)")
                .matcher(str);
        while (m.find()) {
            for (int j = 0; j <= m.groupCount(); j++)
                System.out.println("group(" + j + ")[" + m.group(j) + "]");
        }
    }


    @Test
    public void test2() {
        String str = "家有小宝宝:0.7|家有宝宝:0.7|已婚:0.7|有车:100.0|19-25岁:100.0|男:83.0";
        Pattern p = Pattern.compile("已婚");
        Matcher m = p.matcher(str);
        System.out.println("是否匹配出：" + m.find());
    }

    @Test
    public void test3() {
        String str = "家有小宝宝:0.7|家有宝宝:0.7|已婚:0.7|有车:100.0|1-104岁:100.0|男:83.0";
        Pattern p = Pattern.compile("(\\d|[0-9]{2})\\-([1-9]\\d|1\\d\\d)岁");
        Matcher m = p.matcher(str);
        System.out.println("是否匹配出：" + m.find());
        System.out.println("正则匹配出的数据内容为：" + m.group());
    }

    @Test
    public void test6(){

    }


    public static void main(String[] args) {
        String string = new DateTime().toString("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dateTime = DateTime.parse(string, format);
        System.out.println(dateTime.toString("yyyyMMdd"));
    }


}
