package com.sdyc.hengqin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sdyc.coffee.service.MongoService;
import com.sdyc.coffee.service.WifMacService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import static org.apache.velocity.texen.util.FileUtil.mkdir;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/3 10:15
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description: 探针数据的 controller 接口
 */
@RequestMapping("wifi")
@Controller
public class DetectiveController {

    /**
     * 日志
     */
    public static Log LOG = LogFactory.getLog(DetectiveController.class);


    /**
     * 用来测试 网页连接是否正确
     *
     * @return
     */
    @RequestMapping(value = "/")
    public ModelAndView add(Model model){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }
}
