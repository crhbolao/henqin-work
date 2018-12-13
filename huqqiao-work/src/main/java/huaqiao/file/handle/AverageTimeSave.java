package huaqiao.file.handle;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.storage.StorageLevel;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import scala.Tuple2;
import utils.ListSort;
import utils.PoiModel;
import utils.TimeBeans;

import javax.xml.transform.Source;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/12/7 13:38
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   按所需要求统计平均停留时间，并把结果保存到excel中
 */
public class AverageTimeSave implements Serializable {

    /**
     * 日志
     */
    public Log LOG = LogFactory.getLog(AverageTimeSave.class);

    /**
     * spark context
     */
    public transient JavaSparkContext jsc;

    /**
     * 所有监测的数据
     */
    public String inputPath = "C:\\Users\\sssd\\Desktop\\华侨\\华侨数据\\wifidata\\alldata.txt";

    /**
     * 文件的保存目录
     */
    public String savaPath = "C:\\Users\\sssd\\Desktop\\华侨\\华侨数据\\result\\result.xlsx";

    /**
     * excel标题栏
     */
    public List headTitleLists = new ArrayList();

    /**
     * 用来保存探针的名字
     */
    public HashMap<String, String> wifiMacNames = new HashMap<String, String>();

    public AverageTimeSave() {
        // spark分析初始化
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local[*]").setAppName("AverageTimeAanlysis");
        jsc = new JavaSparkContext(sparkConf);

        // excel文件保存初始化
        headTitleLists.add("停留时间");
        headTitleLists.add("日期");
        headTitleLists.add("wifimac");
        headTitleLists.add("探针名称");
        headTitleLists.add("平均停留时间（ms）");
        headTitleLists.add("总的平均停留时间");
        headTitleLists.add("停留时长min<=15的人数");
        headTitleLists.add("停留时长15<min<=30的人数");
        headTitleLists.add("停留时长30<min<=45的人数");
        headTitleLists.add("停留时长45<min<=60的人数");
        headTitleLists.add("停留时长min>60的人数");

        // 探针名字的初始化
        wifiMacNames.put("C8EEA6386232", "天游峰景区");
        wifiMacNames.put("C8EEA63860DE", "南入口");
        wifiMacNames.put("C8EEA6386196", "大红袍景区");
        wifiMacNames.put("C8EEA6385FE6", "西入口");
    }

    /**
     * 用来获取每段停留的时间
     *
     * @param timeList
     */
    public HashMap<String, Long> getTimes(List<String> timeList) throws Exception {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 原始数据的时间格式
        TimeBeans timeBeans = new TimeBeans();
        HashMap<String, Long> resMap = new HashMap<String, Long>();
        for (int i = 0; i < timeList.size(); i++) {
            String[] split = timeList.get(i).split(",");
            String wifiMac = split[0];
            String time = split[1];
            Date date = sdf.parse(time);
            if (i == 0) {
                timeBeans.setWifiMac(wifiMac);
                timeBeans.setTime(date.getTime());
            } else {
                if (!StringUtils.equalsIgnoreCase(timeBeans.getWifiMac(), wifiMac)) {
                    String[] split1 = timeList.get(i - 1).split(",");
                    String time1 = split1[1];
                    Date date1 = sdf.parse(time1);
                    long error = date1.getTime() - timeBeans.getTime();
                    if (resMap.containsKey(timeBeans.getWifiMac())) {
                        long lastError = resMap.get(timeBeans.getWifiMac()) + error;
                        resMap.put(timeBeans.getWifiMac(), lastError);
                    } else {
                        resMap.put(timeBeans.getWifiMac(), error);
                    }
                    timeBeans.setWifiMac(wifiMac);
                    timeBeans.setTime(date.getTime());
                } else if (i == timeList.size() - 1) {
                    long error = date.getTime() - timeBeans.getTime();
                    if (resMap.containsKey(wifiMac)) {
                        long lastError = resMap.get(wifiMac) + error;
                        resMap.put(wifiMac, lastError);
                    } else {
                        resMap.put(wifiMac, error);
                    }
                }
            }
        }
        return resMap;
    }


    /**
     * 主要是平均停留时间分析
     */
    public void averageTimeAnalysis() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 原始数据的时间格式
        // 读取的原始数据
        JavaRDD<String> sourceRdd = jsc.textFile(inputPath);


        // 用来统计每个手机mac在每个探针中检测到的停留时间
        JavaPairRDD<String, Iterable<Long>> persist = sourceRdd.mapPartitionsToPair(new PairFlatMapFunction<Iterator<String>, String, String>() {
            @Override
            public Iterator<Tuple2<String, String>> call(Iterator<String> iterator) throws Exception {
                LinkedList<Tuple2<String, String>> list = new LinkedList<Tuple2<String, String>>();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    String[] split = next.split(",");
                    String wifiMac = split[1];
                    String phoneMac = split[0];
                    String time = split[2];
                    String[] split1 = time.split(" ");
                    String day = split1[0];
//                    Date date = sdf.parse(time);
                    list.add(new Tuple2<String, String>(day + "," + phoneMac, wifiMac + "," + time));
                }
                return list.iterator();
            }
        }).groupByKey().mapPartitionsToPair(new PairFlatMapFunction<Iterator<Tuple2<String, Iterable<String>>>, String, Long>() {
            @Override
            public Iterator<Tuple2<String, Long>> call(Iterator<Tuple2<String, Iterable<String>>> iterator) throws Exception {
                LinkedList<Tuple2<String, Long>> list = new LinkedList<Tuple2<String, Long>>();
                while (iterator.hasNext()) {
                    Tuple2<String, Iterable<String>> next = iterator.next();
                    String tempStr = next._1();
                    String[] split = tempStr.split(",");
                    String day = split[0];
                    String phoneMac = split[1];
                    Iterator<String> stringIterator = next._2().iterator();
                    List<String> toList = IteratorUtils.toList(stringIterator);
                    if (toList.size() < 2) {
                        continue;
                    }
                    // tocode 用来统计每个wifimac的停留时间
                    HashMap<String, Long> times = getTimes(toList);
                    for (Map.Entry<String, Long> entry : times.entrySet()) {
                        String wifiMac = entry.getKey();
                        Long singleTime = entry.getValue();
                        if (singleTime == 0l) {
                            continue;
                        }
                        list.add(new Tuple2<String, Long>(day + "," + wifiMac, singleTime));
                    }
                }
                return list.iterator();
            }
        }).groupByKey().persist(StorageLevel.MEMORY_AND_DISK_SER_2());

        // 用来统计每天，每个探针的平均停留时间
        Map<String, String> result = persist.mapPartitionsToPair(new PairFlatMapFunction<Iterator<Tuple2<String, Iterable<Long>>>, String, String>() {
            @Override
            public Iterator<Tuple2<String, String>> call(Iterator<Tuple2<String, Iterable<Long>>> iterator) throws Exception {
                LinkedList<Tuple2<String, String>> list = new LinkedList<Tuple2<String, String>>();
                while (iterator.hasNext()) {
                    Tuple2<String, Iterable<Long>> next = iterator.next();
                    Iterator<Long> longIterator = next._2().iterator();
                    List<Long> toList = IteratorUtils.toList(longIterator);
                    Long sum = 0l;
                    for (Long aLong : toList) {
                        sum += aLong;
                    }
                    long average = sum / toList.size();
                    list.add(new Tuple2<String, String>(next._1(), String.valueOf(average)));
                }
                return list.iterator();
            }
        }).collectAsMap();

        // 用力啊统计每个探针，每天，每个时间段的人数
        Map<String, Iterable<String>> result2 = persist.mapPartitionsToPair(new PairFlatMapFunction<Iterator<Tuple2<String, Iterable<Long>>>, String, Integer>() {
            @Override
            public Iterator<Tuple2<String, Integer>> call(Iterator<Tuple2<String, Iterable<Long>>> iterator) throws Exception {
                LinkedList<Tuple2<String, Integer>> list = new LinkedList<Tuple2<String, Integer>>();
                while (iterator.hasNext()) {
                    Tuple2<String, Iterable<Long>> next = iterator.next();
                    String tempStr = next._1();
                    String[] split = tempStr.split(",");
                    String day = split[0];
                    String wifiMac = split[1];
                    Iterator<Long> longIterator = next._2().iterator();
                    List<Long> toList = IteratorUtils.toList(longIterator);
                    for (Long timeLong : toList) {
                        if (timeLong <= 900000) {
                            list.add(new Tuple2<String, Integer>(day + "," + wifiMac + "," + "1", 1));
                        } else if (timeLong > 900000 && timeLong <= 1800000) {
                            list.add(new Tuple2<String, Integer>(day + "," + wifiMac + "," + "2", 1));
                        } else if (timeLong > 1800000 && timeLong <= 2700000) {
                            list.add(new Tuple2<String, Integer>(day + "," + wifiMac + "," + "3", 1));
                        } else if (timeLong > 2700000 && timeLong <= 3600000) {
                            list.add(new Tuple2<String, Integer>(day + "," + wifiMac + "," + "4", 1));
                        } else {
                            list.add(new Tuple2<String, Integer>(day + "," + wifiMac + "," + "5", 1));
                        }
                    }
                }
                return list.iterator();
            }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        }).mapPartitionsToPair(new PairFlatMapFunction<Iterator<Tuple2<String, Integer>>, String, String>() {
            @Override
            public Iterator<Tuple2<String, String>> call(Iterator<Tuple2<String, Integer>> iterator) throws Exception {
                LinkedList<Tuple2<String, String>> list = new LinkedList<Tuple2<String, String>>();
                while (iterator.hasNext()) {
                    Tuple2<String, Integer> next = iterator.next();
                    String tempStr = next._1();
                    String[] split = tempStr.split(",");
                    String day = split[0];
                    String wifiMac = split[1];
                    String lable = split[2];
                    Integer num = next._2();
                    list.add(new Tuple2<String, String>(day + "," + wifiMac, lable + ":" + num));
                }
                return list.iterator();
            }
        }).groupByKey().collectAsMap();
        persist.unpersist();


        // tocode 分析数据的保存
        try {
            savaData(result, result2);
            LOG.info("保存数据成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来保存数据
     *
     * @param result1 每个探针平均每天的停留时间
     * @param result2 每个探针，每天，每个时间段的人数统计
     * @throws Exception
     */
    public void savaData(Map<String, String> result1, Map<String, Iterable<String>> result2) throws Exception {
        // 首先检查文件是否存在，不存在则创建
        File file = new File(savaPath);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        LOG.info("目录初始化成功，开始导出：" + file.getName());

        /**
         * 创建sheet
         */
        // excel 文件的创建
        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        // 创建sheet
        SXSSFSheet sheet = wb.createSheet("平均停留时长");

        /**
         * 字体设置
         */
        // 表头字体
        XSSFFont headFont = (XSSFFont) wb.createFont();
        headFont.setFontName("微软雅黑");
        headFont.setFontHeightInPoints((short) 11);
        headFont.setColor(HSSFColor.WHITE.index);
        //正文字体
        XSSFFont contextFont = (XSSFFont) wb.createFont();
        contextFont.setFontName("微软雅黑");
        contextFont.setFontHeightInPoints((short) 10);
        contextFont.setColor(HSSFColor.BLACK.index);

        /**
         * 表头样式设置1
         */
        XSSFCellStyle headStyle1 = (XSSFCellStyle) wb.createCellStyle();
        //tocode 设置表头颜色
        headStyle1.setFillForegroundColor(new XSSFColor(new java.awt.Color(20, 189, 192)));
        // 让excel应用此格式
        headStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        headStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        // 上下居中
        headStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        headStyle1.setFont(headFont);
        // 左右居中
        headStyle1.setVerticalAlignment(VerticalAlignment.CENTER);

        /**
         * 表头样式设置2
         */
        XSSFCellStyle headStyle2 = (XSSFCellStyle) wb.createCellStyle();
        //tocode 设置表头颜色
        headStyle2.setFillForegroundColor(new XSSFColor(new java.awt.Color(192, 140, 63)));
        // 让excel应用此格式
        headStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headStyle2.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        headStyle2.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        // 上下居中
        headStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        headStyle2.setFont(headFont);
        // 左右居中
        headStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
//        headStyle.setWrapText(true); // 自动换行

        /**
         * 开始写入标题
         */
        // 标题1
        Row row = sheet.createRow(0);
        row.setHeight((short) 450);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headTitleLists.size() - 2));
        Cell cell = row.createCell(0);
        cell.setCellValue(String.valueOf(headTitleLists.get(0)));
        cell.setCellStyle(headStyle1);
        // 标题2
        Row row1 = sheet.createRow(1);
        for (int i = 1; i < headTitleLists.size(); i++) {
            Cell row1Cell = row1.createCell(i - 1);
            row1Cell.setCellValue(String.valueOf(headTitleLists.get(i)));
            row1Cell.setCellStyle(headStyle2);
            sheet.setColumnWidth(i - 1, String.valueOf(headTitleLists.get(i)).getBytes().length * 4 * 64);
        }

        /**
         * 开始导入分析结果，连个结果的大小是相等的
         */
        int lineNum = 2;
        // 取key并按时间进行排序，使保存结果按时间排序
        Set<String> keySet = result1.keySet();
        ArrayList<String> keyList = new ArrayList<String>(keySet);
        Collections.sort(keyList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        for (String key : keyList) {
            SXSSFRow sheetRow = sheet.createRow(lineNum++);
            String[] split = key.split(",");
            String time = split[0];
            String wifiMac = split[1];
            String wifiMacName = wifiMacNames.get(wifiMac);
            String averageTime = result1.get(key);
            Iterable<String> iterable = result2.get(key);
            LinkedList<String> list = sortList(iterable);
            for (int i = 1; i < headTitleLists.size(); i++) {
                sheetRow.createCell(i - 1);
            }
            sheetRow.getCell(0).setCellValue(time);
            sheetRow.getCell(1).setCellValue(wifiMac);
            sheetRow.getCell(2).setCellValue(wifiMacName);
            sheetRow.getCell(3).setCellValue(averageTime);
            sheetRow.getCell(5).setCellValue(list.get(0));
            sheetRow.getCell(6).setCellValue(list.get(1));
            sheetRow.getCell(7).setCellValue(list.get(2));
            sheetRow.getCell(8).setCellValue(list.get(3));
            sheetRow.getCell(9).setCellValue(list.get(4));
        }

        /**
         * 用来求每天的平均值
         */
        int wifiLabel = 3;   // 表示wifimac的种类
        int column = 4;      // excel中需要写入的数据
        LinkedList<String> list = new LinkedList<String>();
        for (int i = 0; i < keyList.size(); i += wifiLabel) {
            Double d1 = Double.valueOf(result1.get(keyList.get(i)));
            Double d2 = Double.valueOf(result1.get(keyList.get(i + 1)));
            Double d3 = Double.valueOf(result1.get(keyList.get(i + 2)));
            list.add(String.valueOf((d1 + d2 + d3) / wifiLabel));
        }
        int rows = sheet.getPhysicalNumberOfRows();
        for (int i = 3; i <= rows; i++) {
            int index = i / wifiLabel;
            SXSSFRow sheetRow = sheet.getRow(i - 1);
            if (sheetRow != null) {
                SXSSFCell rowCell = sheetRow.getCell(column);
                rowCell.setCellValue(list.get(index - 1));
            }
        }

        /**
         * 动态合并同一列中相同的内容
         */
        PoiModel poiModel = new PoiModel();
        for (int index = 0; index < result1.size(); index++) {
            if (index == 0) {
                poiModel.setContent(sheet.getRow(index + 2).getCell(column).getStringCellValue());
                poiModel.setRowIndex(index + 2);
                poiModel.setCellIndex(column);
            } else {
                String cellValue = sheet.getRow(index + 2).getCell(column).getStringCellValue();
                if (!StringUtils.equalsIgnoreCase(poiModel.getContent(), cellValue)) {
                    CellRangeAddress cra = new CellRangeAddress(poiModel.getRowIndex(), index + 1, poiModel.getCellIndex(), poiModel.getCellIndex());
                    sheet.addMergedRegion(cra);
                    poiModel.setContent(cellValue);
                    poiModel.setRowIndex(index + 2);
                    poiModel.setCellIndex(column);
                } else if (index == result1.size() - 1) {
                    CellRangeAddress cra = new CellRangeAddress(poiModel.getRowIndex(), index + 2, poiModel.getCellIndex(), poiModel.getCellIndex());
                    sheet.addMergedRegion(cra);
                }
            }
        }


        FileOutputStream fileOutputStream = new FileOutputStream(file);
        wb.write(fileOutputStream);
        wb.dispose();
        fileOutputStream.flush();
        fileOutputStream.close();

    }

    /**
     * 将统计时间段按顺序排序，方便保存excel.
     *
     * @param iterable
     * @return
     */
    public LinkedList<String> sortList(Iterable<String> iterable) {
        Iterator<String> iterator = iterable.iterator();
        HashMap<String, String> map = new HashMap<String, String>();
        while (iterator.hasNext()) {
            String next = iterator.next();
            String[] split = next.split(":");
            map.put(split[0], split[1]);
        }
        LinkedList<String> resList = new LinkedList<String>();
        resList.add(map.get("1"));
        resList.add(map.get("2"));
        resList.add(map.get("3"));
        resList.add(map.get("4"));
        resList.add(map.get("5"));
        return resList;
    }

    public static void main(String[] args) throws Exception {
        AverageTimeSave averageTimeSave = new AverageTimeSave();
        averageTimeSave.averageTimeAnalysis();
    }

}
