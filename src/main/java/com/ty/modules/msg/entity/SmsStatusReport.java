package com.ty.modules.msg.entity;

import com.ty.common.persistence.DataEntity;

/**
 * Created by tykfkf02 on 2016/9/1.
 */
public class SmsStatusReport extends DataEntity<SmsStatusReport> {
    private String sendTime;
    public SmsStatusReport() {
    }

    public SmsStatusReport(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
