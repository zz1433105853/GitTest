package com.ty.modules.msg.entity;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tykfkf02 on 2017/4/20.
 */
public class Msg implements Serializable {
    private static final long serialVersionUID = 4181814990086062075L;
    private String customerId;
    private String msgRecordId;
    private String ext;
    private String title;
    private String mobile;
    private String content;
    private String isp1;
    private String isp2;
    private String isp3;
    private List<Msg> msgList = Lists.newArrayList();

    public Msg() {
    }

    public Msg(String customerId, String msgRecordId, String ext, String mobile, String content) {
        this.customerId = customerId;
        this.msgRecordId = msgRecordId;
        this.ext = ext;
        this.mobile = mobile;
        this.content = content;
    }

    public Msg(String customerId, String msgRecordId, String ext, String mobile, String content,String title) {
        this.customerId = customerId;
        this.msgRecordId = msgRecordId;
        this.ext = ext;
        this.mobile = mobile;
        this.content = content;
        this.title = title;
    }

    public Msg(String isp1, String isp2, String isp3, List<Msg> msgList) {
        this.isp1 = isp1;
        this.isp2 = isp2;
        this.isp3 = isp3;
        this.msgList = msgList;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMsgRecordId() {
        return msgRecordId;
    }

    public void setMsgRecordId(String msgRecordId) {
        this.msgRecordId = msgRecordId;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsp1() {
        return isp1;
    }

    public void setIsp1(String isp1) {
        this.isp1 = isp1;
    }

    public String getIsp2() {
        return isp2;
    }

    public void setIsp2(String isp2) {
        this.isp2 = isp2;
    }

    public String getIsp3() {
        return isp3;
    }

    public void setIsp3(String isp3) {
        this.isp3 = isp3;
    }

    public List<Msg> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<Msg> msgList) {
        this.msgList = msgList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
