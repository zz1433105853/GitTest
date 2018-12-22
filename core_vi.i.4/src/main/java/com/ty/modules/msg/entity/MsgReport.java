package com.ty.modules.msg.entity;

import com.ty.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by ljb on 2017/4/12 09:54.
 * 状态报告
 */
public class MsgReport extends DataEntity<MsgReport> {

    private static final long serialVersionUID = 5035798783237284597L;
    private Customer customer;
    private String msgId;
    private String mobile;
    private String arrivedStatus;
    private Date arrivedTime;
    private String arrivedResultMessage;
    private String arrivedSendStatus;

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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
