package com.ty.modules.msg.entity;

import com.ty.common.persistence.DataEntity;

/**
 * Created by ljb on 2016/5/26.
 */
public class Message extends DataEntity<Message> {

    private static final long serialVersionUID = 5465892096980807821L;
    private Customer customer;
    private UserSubmit userSubmit;
    private MessageSubmitLog messageSubmitLog;
    private MessageSend messageSend;
    private MessageAuditSend messageAuditSend;
    private CxAuditSend cxAuditSend;
    private MsgRecord msgRecord;

    public Message() {
    }

    public Message(MessageSend messageSend) {
        this.messageSend = messageSend;
    }

    public Message(MessageAuditSend messageAuditSend) {
        this.messageAuditSend = messageAuditSend;
    }

    public Message(String id) {
        super(id);
    }


    public Message(Customer customer, UserSubmit userSubmit,MessageSubmitLog messageSubmitLog) {
        this.customer = customer;
        this.userSubmit = userSubmit;
        this.messageSubmitLog = messageSubmitLog;
    }

    public Message(CxAuditSend auditSend) {
        this.cxAuditSend = auditSend;
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

    public MessageSend getMessageSend() {
        return messageSend;
    }

    public void setMessageSend(MessageSend messageSend) {
        this.messageSend = messageSend;
    }

    public MessageAuditSend getMessageAuditSend() {
        return messageAuditSend;
    }

    public void setMessageAuditSend(MessageAuditSend messageAuditSend) {
        this.messageAuditSend = messageAuditSend;
    }

    public MsgRecord getMsgRecord() {
        return msgRecord;
    }

    public void setMsgRecord(MsgRecord msgRecord) {
        this.msgRecord = msgRecord;
    }

    public CxAuditSend getCxAuditSend() {
        return cxAuditSend;
    }

    public void setCxAuditSend(CxAuditSend cxAuditSend) {
        this.cxAuditSend = cxAuditSend;
    }
}
