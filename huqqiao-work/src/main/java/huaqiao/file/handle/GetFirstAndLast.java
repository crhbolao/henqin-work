package huaqiao.file.handle;

import jdk.nashorn.internal.runtime.ListAdapter;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;
import utils.ListSort;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/12/7 9:33
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  用来获取探针数据的第一次探测时间和最后一次探测时间
 */
public class GetFirstAndLast implements Serializable {

    public String inputPath = "C:\\Users\\sssd\\Desktop\\华侨数据\\wifidata\\alldata.txt";
    public String savePath = "C:\\Users\\sssd\\Desktop\\华侨数据\\wifidata\\wifiSource.csv";
    public transient JavaSparkContext js;

    public GetFirstAndLast() {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("getFirstAndLast");
        sparkConf.setMaster("local[*]");
        js = new JavaSparkContext(sparkConf);
    }


    public void readFile() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        JavaRDD<String> javaRDD = js.textFile(inputPath);
        JavaPairRDD<String, Iterable<Long>> groupByKey = javaRDD.mapPartitionsToPair(new PairFlatMapFunction<Iterator<String>, String, Long>() {
            public Iterator<Tuple2<String, Long>> call(Iterator<String> iterator) throws Exception {
                LinkedList<Tuple2<String, Long>> list = new LinkedList<Tuple2<String, Long>>();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    String[] split = next.split(",");
                    String wifiMac = split[1];
                    String phoneMac = split[0];
                    String time = split[2];
                    if (StringUtils.isBlank(time)) {
                        continue;
                    }
                    Date date = sdf.parse(time);
                    list.add(new Tuple2<String, Long>(wifiMac + "," + phoneMac, date.getTime()));
                }
                return list.iterator();
            }
        }).groupByKey();

        System.out.println(groupByKey.collectAsMap().size());

        Map<String, String> map = groupByKey.mapPartitionsToPair(new PairFlatMapFunction<Iterator<Tuple2<String, Iterable<Long>>>, String, String>() {
            public Iterator<Tuple2<String, String>> call(Iterator<Tuple2<String, Iterable<Long>>> iterator) throws Exception {
                LinkedList<Tuple2<String, String>> list = new LinkedList<Tuple2<String, String>>();
                while (iterator.hasNext()) {
                    Tuple2<String, Iterable<Long>> next = iterator.next();
                    Iterator<Long> stringIterator = next._2().iterator();
                    List<Long> tempList = IteratorUtils.toList(stringIterator);
                    ListSort.sort(tempList, 0, tempList.size() - 1);
                    Long tempStart = tempList.get(0);
                    Long tempEnd = tempList.get(tempList.size() - 1);
                    String startTime = sdf2.format(new Date(tempStart));
                    String endTime = sdf2.format(new Date(tempEnd));
                    list.add(new Tuple2<String, String>(next._1(), startTime + "," + endTime));
                }
                return list.iterator();
            }
        }).collectAsMap();

        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String wifiClien = entry.getKey();
            String value = entry.getValue();
            String line = wifiClien + "," + value + "\n";
            stringBuffer.append(line);
        }

        try {
            FileUtils.writeStringToFile(new File(savePath), stringBuffer.toString(), "GBK", true);
            System.out.println("文件处理完毕！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GetFirstAndLast getFirstAndLast = new GetFirstAndLast();
        getFirstAndLast.readFile();
    }
}
