import org.joda.time.DateTime;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/6 16:44
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class DimensionTest {

    public static void main(String[] args) {
        DateTime dateTime = DateTime.now().minusDays(1);
        System.out.println(dateTime);
        System.out.println(DateTime.now());
    }
    
}
