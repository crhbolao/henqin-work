package example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InsuranceDataExportXLSXExecutor implements Runnable {
    protected static Log LOG = LogFactory.getLog(InsuranceDataExportXLSXExecutor.class);
    private String batchId;
    private String userId;
    private String exportFile;
    private String statisticId;
    private String msgType;
    private String destination;
    private String dataType;
    private List<String> keys;
    private HashMap<String, String> CountryInfoMap;
    private HashMap<String, String> ProvinceInfoMap;
    private HashMap<String, String> CityInfoMap;
    private HashMap<String, String> SourceInfoMap;
    private HashMap<String, String> TopicsInfoMap;
    private ImmutableMap<Object, Object> filedMap;
    private Map<String, Object> ruleMap;
    private Map<String, Object> ruleColumnMap;
    private Map<String, List<String>> ruleNodes;
    private String[] insur;
    private List<String> countField;
    private Integer countNum;
    private Integer locId;


    public InsuranceDataExportXLSXExecutor(ImmutableMap<Object, Object> filedMap, Map<String, Object> ruleMap, Map<String, Object> param, Map<String, Object> ruleColumnMap) {
        this.ruleMap = ruleMap;
        this.msgType = ((String) param.get("msgType"));
        this.destination = ((String) param.get("destination"));
        this.dataType = ((String) param.get("dataType"));
        this.batchId = ((String) param.get("batchId"));
        this.statisticId = ((String) param.get("statisticId"));
        this.userId = ((String) param.get("userId"));
        this.exportFile = ((String) param.get("exportFile"));
        this.keys = (List<String>) param.get("keyss");
        this.ruleColumnMap = ruleColumnMap;
        this.ruleNodes = (Map<String, List<String>>) param.get("ruleNodes");
        this.insur = (String[]) param.get("insur");
        this.countField = (List<String>) param.get("countField");
        this.countNum = (Integer) param.get("countNum");
        this.locId = Integer.parseInt(String.valueOf(param.get("latLon")));

        this.filedMap = filedMap;
    }

    @Override
    public void run() {
        ArrayList<String> strings2 = new ArrayList<String>();
        strings2.addAll(countField);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final List<String> province = Lists.newArrayList("北京","天津","上海","重庆","河北","山西","辽宁","吉林","黑龙江","江苏","浙江","安徽","福建","江西","山东","河南","湖北","湖南","广东","海南","四川","贵州","云南","陕西","甘肃","青海","台湾","内蒙古","广西","西藏","宁夏","新疆","香港","澳门");
        Map jmsDoc = Collections.synchronizedMap(new HashMap());
        Map jmsBody = Collections.synchronizedMap(new HashMap());
        ArrayList<String> strings1 = new ArrayList<String>();
        try {
            File file = new File(this.exportFile);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            LOG.info("目录初始化完成，开始导出:" + this.exportFile);
            LOG.info("导出字段:" + this.keys);
            boolean[] isfirst = {true};
            // excel 文件创建
            SXSSFWorkbook  wb = new SXSSFWorkbook(1000);
            // 创建sheet
            SXSSFSheet sheet = wb.createSheet("导出结果模板");

            // 表头字体
            XSSFFont formulaFont = (XSSFFont) wb.createFont();
            formulaFont.setFontName("微软雅黑");
            formulaFont.setFontHeightInPoints((short) 11);
            formulaFont.setColor(HSSFColor.WHITE.index);

            //正文字体
            XSSFFont formulaFont1 = (XSSFFont) wb.createFont();
            formulaFont1.setFontName("微软雅黑");
            formulaFont1.setFontHeightInPoints((short) 10);
            formulaFont1.setColor(HSSFColor.BLACK.index);

            // 设备表头样式
            XSSFCellStyle cellStyle3 = (XSSFCellStyle) wb.createCellStyle();
            //tocode 设置表头颜色
            cellStyle3.setFillForegroundColor(new XSSFColor(new java.awt.Color(192, 0, 0)));
            // 让excel应用此格式
            cellStyle3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle3.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
            cellStyle3.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
            // 上下居中
            cellStyle3.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            cellStyle3.setFont(formulaFont);
            // 左右居中
            cellStyle3.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle3.setWrapText(true);


            // 不同层级，不同格式
            XSSFCellStyle cellStyle5 = (XSSFCellStyle) wb.createCellStyle();
            cellStyle5.setFillForegroundColor(new XSSFColor(new java.awt.Color(250, 191, 143)));
            cellStyle5.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle5.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
            cellStyle5.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
            cellStyle5.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            cellStyle5.setWrapText(true);

            XSSFCellStyle cellStyle4 = (XSSFCellStyle) wb.createCellStyle();
            cellStyle4.setFillForegroundColor(HSSFColor.WHITE.index);
            cellStyle4.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
            cellStyle4.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
            cellStyle4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle4.setFont(formulaFont1);


            XSSFCellStyle cellStyle6 = (XSSFCellStyle) wb.createCellStyle();
            cellStyle6.setFillForegroundColor(HSSFColor.WHITE.index);
            cellStyle6.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
            cellStyle6.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
            cellStyle6.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle6.setAlignment(HorizontalAlignment.CENTER);
            cellStyle6.setFont(formulaFont1);



            if (isfirst[0]) {
                List titileList = new ArrayList();
                int i = 0;
                //导出结果模板
                Row row = sheet.createRow(0);
                Row row1 = sheet.createRow(1);
                row.setHeight((short)450);
                int roww = 0;

                //tocode keys 是标题，遍历
                ListIterator<String> stringListIterator = keys.listIterator();
                List<String> stringss = new ArrayList<String>();
                while (stringListIterator.hasNext()) {
                    String key = stringListIterator.next();
                    String value = "";
                    if (filedMap.containsKey(key)) {
                        value = String.valueOf(this.filedMap.get(key));
                    }else {
                        value = String.valueOf(ruleMap.get(key));
                    }

                    // 固定死的标题
                    if (roww < insur.length) {
                        if (ruleNodes.containsKey(key)) {
                            stringss = ruleNodes.get(key);
                        }
                        if (ruleNodes.containsKey(key)) {
                            if (stringss.size() > 1) {
                                // addMergedRegion.CellRangeAddress， 用来合并一些单元格
                                //  CellRangeAddress(int, int, int, int) 参数:起始行号,终止行号, 起始列号,终止列号
                                sheet.addMergedRegion(new CellRangeAddress(0, 0, roww, roww + stringss.size() - 1));
                            }
                            Cell cell = row.createCell(roww);
                            cell.setCellValue(value);
                            cell.setCellStyle(cellStyle3);
                            stringListIterator.remove();
                            roww--;
                            i--;
                        }else if (stringss != null && stringss.contains(key)) {
                            Cell cell = row1.createCell(roww);
                            cell.setCellValue(value);
                            cell.setCellStyle(cellStyle5);
                        } else {
                            sheet.addMergedRegion(new CellRangeAddress(0, 1, roww, roww));
                            SXSSFCell cell = (SXSSFCell) row.createCell(i);
                            Cell cell1 = row1.createCell(i);
                            cell1.setCellStyle(cellStyle3);
                            cell.setCellValue(value);
                            cell.setCellStyle(cellStyle3);
                        }
                        roww++;
                    }else if (ruleNodes.containsKey(key)) {
                            List<String> strings = ruleNodes.get(key);
                            if (strings.size() > 1) {
                                sheet.addMergedRegion(new CellRangeAddress(0, 0, i, i + strings.size()-1));
                            }
                            Cell cell = row.createCell(i);
                            cell.setCellValue(value);
                            cell.setCellStyle(cellStyle3);
                            stringListIterator.remove();
                            i--;
                        } else if ("204".equals(key)) {
                            sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
                            Cell cell = row.createCell(i);
                            cell.setCellValue(value);
                            cell.setCellStyle(cellStyle3);
                            Cell cell1 = row1.createCell(i);
                            cell1.setCellStyle(cellStyle3);
                        }else {
                            if (!keys.get(keys.size()-1).equals(key)) {
                                Cell cell1 = row.createCell(i+1);
                                cell1.setCellStyle(cellStyle3);
                            }
                            Cell cell = row1.createCell(i);
                            cell.setCellValue(value);
                            cell.setCellStyle(cellStyle5);
                        }
                    if (value.equals("帖子内容")) {
                        sheet.setColumnWidth(i, 300*50);
                    }else if (value.equals("帖子URN") || value.equals("发帖时间")){
                        sheet.setColumnWidth(i, 50*50);
                    }
                    i++;
                    titileList.add(value);
                }

                ListIterator<String> iterator = strings2.listIterator();
                int index = 0;
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    String value = null;
                    if (filedMap.containsKey(next)) {
                        value = String.valueOf(filedMap.get(next));
                    }else {
                        value = String.valueOf(ruleMap.get(next));
                    }
                    index++;
                }

                String title = "\"" + StringUtils.join(titileList, "\",\"").replace("SOURCE_URN", "SOURCE_NAME").concat("\"\n");
                LOG.info("导出的标题: " + (String) title);

                isfirst[0] = false;

            }
            Set<String> userSet = Collections.synchronizedSet(new HashSet(0));
            AtomicInteger atomicInteger = new AtomicInteger(1);

            Collections.sort(strings1);
            //int index11 = 0;

            /**
             * 模板写文件的地方
             */
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            wb.write(fileOutputStream);
            wb.dispose();
            fileOutputStream.flush();
            fileOutputStream.close();


            jmsDoc.put("msgType", this.msgType);
            jmsBody.put("success", Boolean.valueOf(true));
            jmsBody.put("userId", this.userId);
            jmsBody.put("exportFile", this.exportFile);
            if ("STATISTIC_EXPORT".equalsIgnoreCase(this.msgType))
                jmsBody.put("batchId", this.statisticId);
            else if ("EXPORT_DATA".equalsIgnoreCase(this.msgType)) {
                jmsBody.put("batchId", this.batchId);
            }
            jmsDoc.put("msgText", jmsBody);
            String msg = JSON.toJSONString(jmsDoc);
            LOG.info(msg + "导出任务完成");
        } catch (Exception e) {
            e.printStackTrace();
            jmsDoc.put("msgType", this.msgType);
            jmsBody.put("success", Boolean.valueOf(false));
            jmsBody.put("userId", this.userId);
            jmsBody.put("exportFile", this.exportFile);
            if ("STATISTIC_EXPORT".equalsIgnoreCase(this.msgType))
                jmsBody.put("statisticId", this.statisticId);
            else if ("EXPORT_DATA".equalsIgnoreCase(this.msgType)) {
                jmsBody.put("batchId", this.batchId);
            }
            jmsBody.put("error", ExceptionUtils.getFullStackTrace(e));
            jmsDoc.put("msgText", jmsBody);
            String msg = JSON.toJSONString(jmsDoc);
            LOG.info(msg + "导出任务完成");
            LOG.info(ExceptionUtils.getFullStackTrace(e));
        }
    }

    public boolean getNodes(List<String> longs, Map<String, Serializable> meta1) {
        boolean continar = false;
        for (String aLong : longs) {
            if (ruleNodes.containsKey(aLong)) {
                List<String> strings = ruleNodes.get(aLong);
                boolean nodes = getNodes(strings, meta1);
                if (nodes == true) {
                    return nodes;
                }
            }else {
                String serializable = (String) meta1.get("COL_" + aLong);
                if ("Y".equalsIgnoreCase(serializable)) {
                    continar = true;
                    return continar;
                }
            }
        }
        return continar;
    }

    public void createdCell(Row row,List<Object> datalist,XSSFCellStyle cellStyle4) {
        for (int i = 0; i < datalist.size(); i++) {

            Cell cell = row.createCell(i);
            Object o = datalist.get(i);
            if (o instanceof Number) {
                cell.setCellValue((Integer) datalist.get(i));
            } else {
                cell.setCellValue(String.valueOf(datalist.get(i)));
            }
            cell.setCellStyle(cellStyle4);
        }
    }

    public void createdCell1(Row row,List<Object> datalist,XSSFCellStyle cellStyle4, int i1) {
        for (int i = 0; i < datalist.size(); i++) {
            Object o = datalist.get(i);
            if (row != null) {
                Cell cell1 = row.createCell(i);
                if (i == 0) {
                    cell1.setCellValue(i1);
                }else{
                    if (o instanceof Number) {
                        cell1.setCellValue((Integer)datalist.get(i));
                    }else {
                        cell1.setCellValue(String.valueOf(datalist.get(i)));
                    }
                }
                cell1.setCellStyle(cellStyle4);
            }
        }
    }

    /**
     * 获取两地的距离
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     */
    public double getRangeByLat(double lat1, double lon1, double lat2, double lon2) {
        //d = R × Arc Cos[sinlon1sinlon2+ coslon1coslon2cos(lat1−lat2)]
        int r = 6371;
        Double range = r * Math.acos(Math.sin(lon1)*Math.sin(lon2) + Math.cos(lon1) * Math.cos(lon2) * Math.cos(lat1 - lat2));
        return range;
    }

}
