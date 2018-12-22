package com.ty.modules.tunnel.send.container.entity;

import com.ty.common.persistence.DataEntity;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.MsgCxRecord;
import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.msg.entity.MsgResponse;
import com.ty.modules.msg.entity.Tunnel;
import org.apache.log4j.Logger;

/**
 * Created by 阿水 on 2017/4/12 16:10.
 */
public abstract class AbstractMessageSend extends DataEntity<AbstractMessageSend> implements MessageSend {

    private static final long serialVersionUID = -2522591311097618142L;

    private static Logger logger= Logger.getLogger(AbstractMessageSend.class);

    protected MsgRecord msgRecord;//对应来自核心系统的发送记录

    protected MsgCxRecord msgCxRecord;

    protected Tunnel tunnel;//发送该消息使用的通道实体（发送时候设置）

    protected String srcId;//具体发送时候设置该srcId（发送时候设置）

    public AbstractMessageSend(MsgRecord msgRecord) {
        this.msgRecord = msgRecord;
    }

    public AbstractMessageSend(MsgCxRecord msgRecord) {
        this.msgCxRecord = msgRecord;
    }

    /**
     * 生成该发送的返回实体
     * @return
     */
    @Override
    public MsgResponse getMsgResponse() {
        if(msgRecord==null) return null;//如果发送记录都是空的，那本次发送消息响应结果为空
        MsgResponse response = new MsgResponse();
        response.setMsgRecord(msgRecord);
        response.setTunnel(tunnel);
        response.setSrcId(srcId);
        response.setRemarks(remarks);

        String msgContent = getMsgContent();
        String msgMobile = getMsgMobile();
        String msgId = getMsgId();
        String seqId = getSeqId();
        String result = getResult();
        String resulltOfTunnel = getResultMessage();

        logger.info(StringUtils.builderString("\nMobile: ",msgMobile,"\n"
                ,"Content: ",msgContent,"\n"
                ,"MsgId: ",msgId,"\n"
                ,"SeqId: ",seqId,"\n"
                ,"SrcId: ",srcId,"\n"
                ,"Result: ",resulltOfTunnel,"\n"));

        response.setMsgId(msgId);
        response.setSequenceNumber(Integer.valueOf(seqId));
        response.setSendStatus(result);
        response.setSendResultMessage(resulltOfTunnel);

        return response;
    }

    /**
     * 获取本次发送短信内容
     * @return
     */
    public String getContent() {
        String result = "";
        if(msgRecord!=null && (StringUtils.isNotBlank(msgRecord.getId()) || (msgRecord.getMsgRecordList()!=null && !msgRecord.getMsgRecordList().isEmpty())) && StringUtils.isNotBlank(msgRecord.getContent())) {
            result = msgRecord.getContent();
        }
        return result;
    }

    /**
     * 获取本次发送短信的手机号
     * @return
     */
    public String getMobile() {
        String result = "";
        if(msgRecord!=null && (StringUtils.isNotBlank(msgRecord.getId()) || (msgRecord.getMsgRecordList()!=null && !msgRecord.getMsgRecordList().isEmpty())) && StringUtils.isNotBlank(msgRecord.getMobile())) {
            result = msgRecord.getMobile();
        }
        return result;
    }

    @Override
    public String getRecordId() {
        if(msgRecord!=null && StringUtils.isNotBlank(msgRecord.getId())) {
            return msgRecord.getId();
        }else {
            return "<无效发送记录>";
        }
    }

    public MsgRecord getMsgRecord() {
        return msgRecord;
    }

    public void setMsgRecord(MsgRecord msgRecord) {
        this.msgRecord = msgRecord;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public Tunnel getTunnel() {
        return tunnel;
    }

    public void setTunnel(Tunnel tunnel) {
        this.tunnel = tunnel;
    }

    public MsgCxRecord getMsgCxRecord() {
        return msgCxRecord;
    }

    public void setMsgCxRecord(MsgCxRecord msgCxRecord) {
        this.msgCxRecord = msgCxRecord;
    }
}
