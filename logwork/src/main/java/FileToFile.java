import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/11/22 15:45
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   java 实现将一个文件移动到别的而文件
 */
public class FileToFile {

    /**
     * 待更改目录的文件
     */
    public String sourcePath = "C:\\Users\\sssd\\Desktop\\横琴二次导出\\sourcedata";

    /**
     * 更改目录的目标文件
     */
    public String targetPath = "C:\\Users\\sssd\\Desktop\\横琴二次导出\\data2";


    public FileToFile() {
        // 如果目录文件不存在，创建
        File file = new File(targetPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 更改文件的目录（注意区别复制）
     */
    public void changFileDir() {
        File file = new File(sourcePath);
        File[] files = file.listFiles();
        // 遍历该目录下的所有子目录
        for (File subFile : files) {
            if (subFile.isDirectory()) {
                File[] subSubFile = subFile.listFiles();
                // 遍历子目录下的所有文件
                for (File tempFile : subSubFile) {
                    File newFile = new File(targetPath + "\\" + tempFile.getName());
                    // 将文件重命名
                    tempFile.renameTo(newFile);
                }
            }
        }
    }

    public static void main(String[] args) {
        FileToFile fileToFile = new FileToFile();
        fileToFile.changFileDir();
    }
}
