package com.ty.modules.msg.entity;

import com.ty.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by 阿水 on 2017/4/12 09:54.
 * 发送响应
 */
public class RedisMsgResponse extends DataEntity<RedisMsgResponse> {

    private static final long serialVersionUID = -3780827279299871276L;
    private String sendRecordId;
    private String msgId;
    private String srcId;
    private String tunnelId;
    private int sequenceNumber;
    private String sendStatus;
    private String sendResultMessage;

    public RedisMsgResponse() {

    }

    public RedisMsgResponse(String id, String sendRecordId, String msgId, String srcId,
                            String tunnelId, int sequenceNumber, String sendStatus,
                            String sendResultMessage, Date createDate, Date updateDate, String remarks) {
        this.id = id;
        this.sendRecordId = sendRecordId;
        this.msgId = msgId;
        this.srcId = srcId;
        this.tunnelId = tunnelId;
        this.sequenceNumber = sequenceNumber;
        this.sendStatus = sendStatus;
        this.sendResultMessage = sendResultMessage;
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

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getSendResultMessage() {
        return sendResultMessage;
    }

    public void setSendResultMessage(String sendResultMessage) {
        this.sendResultMessage = sendResultMessage;
    }

    public String getSendRecordId() {
        return sendRecordId;
    }

    public void setSendRecordId(String sendRecordId) {
        this.sendRecordId = sendRecordId;
    }

    public String getTunnelId() {
        return tunnelId;
    }

    public void setTunnelId(String tunnelId) {
        this.tunnelId = tunnelId;
    }
}
