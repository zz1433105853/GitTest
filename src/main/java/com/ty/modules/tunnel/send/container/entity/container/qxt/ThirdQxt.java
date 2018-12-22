package com.ty.modules.tunnel.send.container.entity.container.qxt;

import org.apache.http.client.utils.URLEncodedUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by tykfkf02 on 2017/7/22.
 */
@XmlRootElement(name="APIResult",namespace = "http://tempuri.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdQxt {
    private int Code;
    private String Result;

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }
}
