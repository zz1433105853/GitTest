package com.ty.modules.msg.entity;

import com.google.common.collect.Lists;
import com.ty.common.persistence.DataEntity;
import com.ty.common.utils.StringUtils;

import java.util.List;

/**
 * Created by 阿水 on 2017/4/12 09:54.
 * 发送响应
 */
public class MsgResponse extends DataEntity<MsgResponse> {

    private static final long serialVersionUID = -8931125721969116696L;

    private MsgRecord msgRecord;
    private String msgId;
    private String srcId;
    private Tunnel tunnel;
    private int sequenceNumber;
    private String sendStatus;
    private String sendResultMessage;

    public MsgRecord getMsgRecord() {
        return msgRecord;
    }

    public void setMsgRecord(MsgRecord msgRecord) {
        this.msgRecord = msgRecord;
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

    public Tunnel getTunnel() {
        return tunnel;
    }

    public void setTunnel(Tunnel tunnel) {
        this.tunnel = tunnel;
    }

    public List<MsgResponse> toMsgResponseList() {
        if(isPackageSend()) {
            List<MsgRecord> mrList = msgRecord.getMsgRecordList();
            List<com.ty.modules.msg.entity.MsgResponse> msgResponseList = Lists.newArrayList();
            for(MsgRecord m : mrList) {
                com.ty.modules.msg.entity.MsgResponse mNew = new com.ty.modules.msg.entity.MsgResponse();
                mNew.setMsgRecord(m);
                mNew.setMsgId(getMsgId());
                mNew.setSendResultMessage(getSendResultMessage());
                mNew.setSendStatus(getSendStatus());
                mNew.setSequenceNumber(getSequenceNumber());
                mNew.setSrcId(getSrcId());
                mNew.setTunnel(getTunnel());
                mNew.preInsert();
                msgResponseList.add(mNew);
            }
            return msgResponseList;
        }else {
            return Lists.newArrayList();
        }
    }

    public boolean isPackageSend() {
        if(msgRecord!=null && StringUtils.isBlank(msgRecord.getId()) && !msgRecord.getMsgRecordList().isEmpty()) {
            return true;
        }
        return false;
    }
}
