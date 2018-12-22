package com.ty.modules.core.common.disruptor.message;

import com.ty.modules.msg.entity.*;

/**
 * Created by tykfkf02 on 2016/11/21.
 */
public class MessageCore {
    private String productName;
    private Customer customer;
    private UserSubmit userSubmit;
    private MessageSubmitLog messageSubmitLog;
    private String content;
    private String mobile;
    private MsgRecord msgRecord;
    private MessageSend messageSend;

    public MessageCore(String productName,Customer customer, UserSubmit userSubmit, MessageSubmitLog messageSubmitLog) {
        this.productName = productName;
        this.customer = customer;
        this.userSubmit = userSubmit;
        this.messageSubmitLog = messageSubmitLog;
    }
    public MessageCore(String productName,Customer customer, UserSubmit userSubmit,String content, MessageSubmitLog messageSubmitLog,String mobile) {
        this.productName = productName;
        this.customer = customer;
        this.userSubmit = userSubmit;
        this.messageSubmitLog = messageSubmitLog;
        this.content = content;
        this.mobile = mobile;
    }

    public MessageCore(String productName,MessageSend messageSend) {
        this.messageSend = messageSend;
        this.productName = productName;
    }

    public MessageCore(String productName, MessageSubmitLog messageSubmitLog) {
        this.productName = productName;
        this.messageSubmitLog = messageSubmitLog;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public UserSubmit getUserSubmit() {
        return userSubmit;
    }

    public void setUserSubmit(UserSubmit userSubmit) {
        this.userSubmit = userSubmit;
    }

    public MessageSubmitLog getMessageSubmitLog() {
        return messageSubmitLog;
    }

    public void setMessageSubmitLog(MessageSubmitLog messageSubmitLog) {
        this.messageSubmitLog = messageSubmitLog;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public MessageSend getMessageSend() {
        return messageSend;
    }

    public void setMessageSend(MessageSend messageSend) {
        this.messageSend = messageSend;
    }

    public MsgRecord getMsgRecord() {
        return msgRecord;
    }

    public void setMsgRecord(MsgRecord msgRecord) {
        this.msgRecord = msgRecord;
    }
}
