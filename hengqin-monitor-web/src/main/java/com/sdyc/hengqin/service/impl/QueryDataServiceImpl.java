package com.sdyc.hengqin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdyc.hengqin.service.QueryDataService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/12/13 16:37
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class QueryDataServiceImpl implements QueryDataService {


    public JdbcTemplate jdbcTemplate;


    /**
     * 根据筛选条件用来查找数据，（默认查找的是所有的探针，时间范围是五天内）
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return
     */
    @Override
    public List<JSONObject> query(String startTime, String endTime) {

        String sql = "select * from monitordata where lastUpdateTime between ? and ?";
        List<JSONObject> query = jdbcTemplate.query(sql, new Object[]{startTime, endTime}, new RowMapper<JSONObject>() {
            @Override
            public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
                JSONObject jsonObject = new JSONObject();
                String[] split = rs.getString(1).split("_");
                String day = split[0];
                String wifiMac = split[1];
                String num = rs.getString(2);
                jsonObject.put("day", day);
                jsonObject.put("wifiMac", wifiMac);
                jsonObject.put("num", num);
                return jsonObject;
            }
        });
        return query;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        QueryDataServiceImpl queryDataService = (QueryDataServiceImpl) context.getBean("queryDataService");
        String startTime = "2018-12-10 16:10:00";
        String endTime = "2018-12-12 16:10:00";
        queryDataService.query(startTime, endTime);
    }

}
