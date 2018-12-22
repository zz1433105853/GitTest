package com.ty.modules.msg.entity;

import com.google.common.collect.Lists;
import com.ty.common.persistence.DataEntity;
import com.ty.common.utils.DateUtils;
import com.ty.common.utils.StringUtils;

import java.util.List;

/**
 * Created by 阿水 on 2017/4/12 09:54.
 * 发送响应
 */
public class MsgCxResponse extends DataEntity<MsgCxResponse> {

    private static final long serialVersionUID = -8931125721969116696L;

    private MsgCxRecord msgCxRecord;
    private String msgId;
    private String srcId;
    private CxTunnel cxTunnel;
    private int sequenceNumber;
    private String sendStatus;
    private String sendResultMessage;

    public MsgCxRecord getMsgCxRecord() {
        return msgCxRecord;
    }

    public void setMsgCxRecord(MsgCxRecord msgCxRecord) {
        this.msgCxRecord = msgCxRecord;
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

    public CxTunnel getCxTunnel() {
        return cxTunnel;
    }

    public void setCxTunnel(CxTunnel cxTunnel) {
        this.cxTunnel = cxTunnel;
    }

    public List<MsgCxResponse> toMsgResponseList() {
        if(isPackageSend()) {
            List<MsgCxRecord> mrList = msgCxRecord.getMsgRecordList();
            List<MsgCxResponse> msgResponseList = Lists.newArrayList();
            for(MsgCxRecord m : mrList) {
                MsgCxResponse mNew = new MsgCxResponse();
                mNew.setMsgCxRecord(m);
                mNew.setMsgId(getMsgId());
                mNew.setSendResultMessage(getSendResultMessage());
                mNew.setSendStatus(getSendStatus());
                mNew.setSequenceNumber(getSequenceNumber());
                mNew.setSrcId(getSrcId());
                mNew.setCxTunnel(getCxTunnel());
                mNew.preInsert();
                msgResponseList.add(mNew);
            }
            return msgResponseList;
        }else {
            return Lists.newArrayList();
        }
    }

    public boolean isPackageSend() {
        if(msgCxRecord!=null && StringUtils.isBlank(msgCxRecord.getId()) && !msgCxRecord.getMsgRecordList().isEmpty()) {
            return true;
        }
        return false;
    }

    public StringBuilder generateSql(List<MsgCxResponse> msgCxResponses){
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ty_sms_cx_send_response")
                .append(" (id, cx_send_record_id, msg_id,src_id, cx_tunnel_id, sequence_number, send_status, send_result_message, create_date,")
                .append(" update_date)")
                .append(" values ");
        for (MsgCxResponse m : msgCxResponses) {
                sql.append("(").append(StringUtils.sqlParameterHandle(m.getId())).append(",");
                sql.append(StringUtils.sqlParameterHandle(m.getMsgCxRecord().getId())).append(",");
                sql.append(StringUtils.sqlParameterHandle(m.getMsgId())).append(",");
                sql.append(StringUtils.sqlParameterHandle(m.getSrcId())).append(",");
                sql.append(StringUtils.sqlParameterHandle(m.getCxTunnel().getId())).append(",");
                sql.append(m.getSequenceNumber()).append(",");
                sql.append(StringUtils.sqlParameterHandle(m.getSendStatus())).append(",");
                sql.append(StringUtils.sqlParameterHandle(m.getSendResultMessage())).append(",");
                sql.append(StringUtils.sqlParameterHandle(DateUtils.formatDateTime(m.getCreateDate()))).append(",");
                sql.append(StringUtils.sqlParameterHandle(DateUtils.formatDateTime(m.getUpdateDate()))).append("),");
        }
        return sql.deleteCharAt(sql.length() - 1);
    }
}
