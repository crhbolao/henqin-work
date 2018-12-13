package com.mongo.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/14 14:41
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public interface SavaDataService {

    /**
     * 将json格式的数据导入到mongo
     *
     * @param jsonObject
     */
    public void saveDataToMongo(JSONObject jsonObject);

}
