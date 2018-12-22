package com.ty.modules.msg.entity;

import com.ty.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by 阿水 on 2017/4/12 09:54.
 * 状态报告
 */
public class RedisMsgReport extends DataEntity<RedisMsgReport> {

    private static final long serialVersionUID = -3320151784044464000L;
    private String customerId;
    private String msgId;
    private String mobile;
    private String arrivedStatus;
    private Date arrivedTime;
    private String arrivedResultMessage;
    private String arrivedSendStatus;

    public RedisMsgReport() {
    }

    public RedisMsgReport(String id, String customerId, String msgId, String mobile,
                          String arrivedStatus, Date arrivedTime, String arrivedResultMessage,
                          String arrivedSendStatus,Date createDate,Date updateDate, String remarks) {
        this.id = id;
        this.customerId = customerId;
        this.msgId = msgId;
        this.mobile = mobile;
        this.arrivedStatus = arrivedStatus;
        this.arrivedTime = arrivedTime;
        this.arrivedResultMessage = arrivedResultMessage;
        this.arrivedSendStatus = arrivedSendStatus;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.remarks = remarks;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getArrivedStatus() {
        return arrivedStatus;
    }

    public void setArrivedStatus(String arrivedStatus) {
        this.arrivedStatus = arrivedStatus;
    }

    public Date getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(Date arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public String getArrivedResultMessage() {
        return arrivedResultMessage;
    }

    public void setArrivedResultMessage(String arrivedResultMessage) {
        this.arrivedResultMessage = arrivedResultMessage;
    }

    public String getArrivedSendStatus() {
        return arrivedSendStatus;
    }

    public void setArrivedSendStatus(String arrivedSendStatus) {
        this.arrivedSendStatus = arrivedSendStatus;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
