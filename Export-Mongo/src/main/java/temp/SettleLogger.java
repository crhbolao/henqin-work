package temp;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/15 11:38
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class SettleLogger implements com.jcraft.jsch.Logger {

    public boolean isEnabled(int level) {
        return true;
    }

    public void log(int level, String msg) {
        System.out.println(msg);
    }
}
