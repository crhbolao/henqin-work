package mine.test;

import org.junit.Test;
import scala.runtime.TraitSetter;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/12/10 13:43
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  用来自定义测试
 */
public class MineTest {

    @Test
    public void test1(){
        ArrayList<String> list = new ArrayList<String>();
        list.add("2018-11-23,C8EEA63860DE");
        list.add("2018-11-19,C8EEA63860DE");
        list.add("2018-11-20,C8EEA63860DE");
        list.add("2018-11-20,C8EEA6386232");
        list.add("2018-11-20,C8EEA6386196");
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        System.out.println(list);
    }

    @Test
    public void test2(){
        int a = 5;
        int b = 3;
        System.out.println(a/b);
    }

}
