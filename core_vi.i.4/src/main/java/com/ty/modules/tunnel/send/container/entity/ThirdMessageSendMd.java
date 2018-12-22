package com.ty.modules.tunnel.send.container.entity;

import com.ty.modules.msg.entity.MsgCxRecord;
import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.tunnel.send.container.entity.container.md.ThirdMdSendMsgResult;

/**
 * Created by ljb on 2017/4/12 16:59.
 */
public class ThirdMessageSendMd extends AbstractThirdPartyMessageSend{

    private static final long serialVersionUID = -3515930675597951093L;
    private ThirdMdSendMsgResult thirdMdSendMsgResult;

    public ThirdMessageSendMd(MsgRecord msgRecord) {
        super(msgRecord);
    }

    public ThirdMessageSendMd(MsgCxRecord msgCxRecord) {
        super(msgCxRecord);
    }

    @Override
    public String getMsgContent() {
        return getContent();
    }

    @Override
    public String getMsgMobile() {
        return getMobile();
    }

    @Override
    public String getMsgId() {
        String msgId = "<无效的响应>";
        if(thirdMdSendMsgResult!=null) {
            msgId = thirdMdSendMsgResult.getRrid();
        }
        return msgId;
    }

    @Override
    public String getSeqId() {
        return "0";
    }

    @Override
    public String getResult() {
        String result = "<无效的响应>";
        if(thirdMdSendMsgResult!=null) {
            result = thirdMdSendMsgResult.isSuccess()?"1":"2";
        }
        return result;
    }

    @Override
    public String getResultMessage() {
        String resulltOfTunnel = "<无效的响应>";
        if(thirdMdSendMsgResult!=null) {
            resulltOfTunnel = thirdMdSendMsgResult.toString();
        }
        return resulltOfTunnel;
    }

    public ThirdMdSendMsgResult getThirdMdSendMsgResult() {
        return thirdMdSendMsgResult;
    }

    public void setThirdMdSendMsgResult(ThirdMdSendMsgResult thirdMdSendMsgResult) {
        this.thirdMdSendMsgResult = thirdMdSendMsgResult;
    }
}
