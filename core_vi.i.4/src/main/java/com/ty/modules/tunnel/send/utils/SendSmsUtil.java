package com.ty.modules.tunnel.send.utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.ty.modules.msg.entity.UserSubmit;
import org.apache.log4j.Logger;

/**
 * 发送短信
 * Created by tykfkf02 on 2016/7/12.
 */
public class SendSmsUtil {
    private Logger logger= Logger.getLogger(SendSmsUtil.class);
    public static void sendMessage(String url,UserSubmit submit) throws UnirestException {
        HttpResponse<String> responseResult = Unirest.post(url)
                .field("sn", submit.getSn())
                .field("password",submit.getPassword())
                .field("mobile", submit.getMobile())
                .field("content", submit.getContent())
                .field("ext", submit.getExt())
                .field("sendTime", submit.getsendTime()).asString();
    }

}
