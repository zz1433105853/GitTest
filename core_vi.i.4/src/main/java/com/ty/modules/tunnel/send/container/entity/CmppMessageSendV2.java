package com.ty.modules.tunnel.send.container.entity;

import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.tunnel.send.container.entity.cmpp.MsgSubmitRespV2;
import com.ty.modules.tunnel.send.container.entity.cmpp.MsgSubmitV2;

/**
 * Created by 阿水 on 2017/4/12 15:20.
 * CMPP发送短信实体
 */
public class CmppMessageSendV2 extends AbstractCmppMessageSend {

    private static final long serialVersionUID = -5774342106595820510L;
    private MsgSubmitV2 msgSubmitNow;
    private MsgSubmitRespV2 msgSubmitResp;

    public CmppMessageSendV2(MsgRecord msgRecord) {
        super(msgRecord);
    }

    @Override
    public String getMsgContent() {
        String msgContent = "<无效的发送记录>";
        if(msgSubmitNow!=null) {
            try {
                msgContent = new String(msgSubmitNow.getMsgContent(),"GBK");
            }catch (Exception e) {
                msgContent = "<编码内容失败>";
            }
        }
        return msgContent;
    }

    @Override
    public String getMsgMobile() {
        String msgMobile = "<无效的发送记录>";
        if(msgSubmitNow!=null) {
            msgMobile = msgSubmitNow.getDestTerminalId();
        }
        return msgMobile;
    }

    @Override
    public String getMsgId() {
        String msgId = "<无效的发送响应>";
        if(msgSubmitResp!=null) {
            msgId = String.valueOf(msgSubmitResp.getMsgId());
        }
        return msgId;
    }

    @Override
    public String getSeqId() {
        String seqId = "<无效的发送记录>";
        if(msgSubmitNow!=null) {
            seqId = String.valueOf(msgSubmitNow.getSequenceId());
        }
        return seqId;
    }

    @Override
    public String getResult() {
        String result = "<无效的发送响应>";
        if(msgSubmitResp!=null) {
            result = "0".equals(String.valueOf(msgSubmitResp.getResult()))? "1" : "2";
        }
        return result;
    }

    @Override
    public String getResultMessage() {
        String resulltOfTunnel = "<无效的发送响应>";
        if(msgSubmitResp!=null) {
            resulltOfTunnel = String.valueOf(msgSubmitResp.getResult());
        }
        return resulltOfTunnel;
    }


    public MsgSubmitV2 getMsgSubmitNow() {
        return msgSubmitNow;
    }

    public void setMsgSubmitNow(MsgSubmitV2 msgSubmitNow) {
        this.msgSubmitNow = msgSubmitNow;
    }

    public MsgSubmitRespV2 getMsgSubmitResp() {
        return msgSubmitResp;
    }

    public void setMsgSubmitResp(MsgSubmitRespV2 msgSubmitResp) {
        this.msgSubmitResp = msgSubmitResp;
    }


}

