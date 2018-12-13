package com.sdyc.hengqin.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/12/13 16:34
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   用来在数据库中查找数据
 */
public interface QueryDataService {

    /**
     * 根据条件查找数据
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return
     */
    public List<JSONObject> query(String startTime, String endTime);

}
