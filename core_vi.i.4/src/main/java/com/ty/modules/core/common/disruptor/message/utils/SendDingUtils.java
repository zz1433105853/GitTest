package com.ty.modules.core.common.disruptor.message.utils;

import com.drew.lang.StringUtil;
import com.google.common.collect.Maps;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.ty.common.mapper.JsonMapper;
import com.ty.common.utils.PropertiesLoader;
import com.ty.common.utils.StringUtils;
import com.ty.modules.core.common.disruptor.message.MessageEventHandler;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import com.mashape.unirest.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Map;

/**
 * 发送钉钉消息
 * Created by ljb on 2017/7/15.
 */
public class SendDingUtils {
    /**
     * 属性文件加载对象
     */
    private static PropertiesLoader loader = new PropertiesLoader("system.properties");
    private static Logger logger = Logger.getLogger(SendDingUtils.class);

    public static void sendDingMsg(String msg){
        Map<String,Object> map = Maps.newHashMap();
        map.put("msgtype","text");

        Map<String,String> contentMap = Maps.newHashMap();
        contentMap.put("content",msg);
        map.put("text",contentMap);

        String mobiles = loader.getProperty("ding_mobiles");
        if(StringUtils.isNotBlank(mobiles)){
            String[] strings = mobiles.split(",");
            Map<String,String[]> atMap = Maps.newHashMap();
            atMap.put("atMobiles",strings);
            map.put("at",atMap);
        }

        String sendContent = JsonMapper.toJsonString(map);

        HttpResponse<String> responseResult = null;
        String result = null;
        try {
            String WEBHOOK_TOKEN = loader.getProperty("webhook_token");
            responseResult = Unirest.post(WEBHOOK_TOKEN)
                    .header("Content-Type","application/json; charset=utf-8")
                    .body(sendContent)
                    .asString();
            result = responseResult.getBody();
            logger.info("钉钉返回结果："+result);
        } catch (UnirestException e) {
            logger.info("钉钉返回异常结果："+result);
        }
    }

}
