package service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import service.SavaDataService;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/14 14:41
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class SavaDataServiceImpl implements SavaDataService {

    /**
     * 日志
     */
    public Log LOG = LogFactory.getLog(SavaDataServiceImpl.class);

    /**
     * mongoTempLate
     */
    protected MongoTemplate mongoTemplate;

    /**
     * 将json格式的数据导入到mongo中
     *
     * @param jsonObject json格式的数据
     */
    @Override
    public void saveDataToMongo(final JSONObject jsonObject) {

        try {
            this.mongoTemplate.execute("tanzhenmac", new CollectionCallback<Object>() {
                @Override
                public Object doInCollection(DBCollection dbCollection) throws MongoException, DataAccessException {

                    final String site = jsonObject.getString("site");
                    final String clientMac = jsonObject.getString("clientMac");
                    String time = jsonObject.getString("updateTime");
                    DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                    DateTime dateTime = DateTime.parse(time, format);
                    final String wifiMac = jsonObject.getString("wifiLicense");


                    final Update update = new Update();
                    final BasicDBObject value = new BasicDBObject();

                    // mongo data 数组中存放的数据
                    value.put("wifiMac", wifiMac);
                    value.put("updatedAt", String.valueOf(convertMills(time)));

                    // Mongo update
                    update.addToSet("wifimacs", wifiMac)
                            .addToSet("sites", site)
                            .addToSet("dates", dateTime.toString("yyyyMMdd"))
                            .set("clientMac", clientMac)
                            .set("lastUpdate", new DateTime().toString("yyyy-MM-dd HH:mm:ss"))
                            .push("data", value);

                    dbCollection.update(
                            Query.query(Criteria.where("_id").is(clientMac)).getQueryObject(),
                            update.getUpdateObject()
                            , true, false);
                    return null;
                }
            });
        } catch (Exception e) {
            LOG.info("更新mongo失败！");
        }
    }

    /**
     * 将String类型的dateTimeStr 转换为 long
     *
     * @param dateTimeStr String 类型的 dateTimeStr
     * @return
     */
    public long convertMills(String dateTimeStr) {
        final String s = dateTimeStr.replaceAll("/", "-");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = System.currentTimeMillis();
        try {
            time = format.parse(s).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContextTemp.xml");
        SavaDataServiceImpl savaDataService = (SavaDataServiceImpl) context.getBean("savaDataService");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("site", "1,201");
        jsonObject.put("clientMac", "22");
        jsonObject.put("wifiLicense", "151515");
        jsonObject.put("updateTime", "");
        savaDataService.saveDataToMongo(jsonObject);
    }
}
