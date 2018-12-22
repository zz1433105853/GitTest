package com.ty.modules.tunnel.send.container.entity;

import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.tunnel.send.container.entity.container.ah.ThirdAhSendMsgResult;

/**
 * Created by 阿水 on 2017/4/22 09:58.
 */
public class ThirdMessageSendAh extends AbstractThirdPartyMessageSend {

    private static final long serialVersionUID = 1072180284919149161L;

    private ThirdAhSendMsgResult thirdAhSendMsgResult;

    public ThirdMessageSendAh(MsgRecord msgRecord) {
        super(msgRecord);
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
        if(thirdAhSendMsgResult.getTaskID()!=null) {
            msgId = thirdAhSendMsgResult.getTaskID();
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
        if(thirdAhSendMsgResult!=null) {
            result = "Success".equals(thirdAhSendMsgResult.getReturnstatus())?"1":"2";
        }
        return result;
    }

    @Override
    public String getResultMessage() {
        String resulltOfTunnel = "<无效的响应>";
        if(thirdAhSendMsgResult!=null) {
            resulltOfTunnel = thirdAhSendMsgResult.getReturnstatus();
        }
        return resulltOfTunnel;
    }

    public ThirdAhSendMsgResult getThirdAhSendMsgResult() {
        return thirdAhSendMsgResult;
    }

    public void setThirdAhSendMsgResult(ThirdAhSendMsgResult thirdAhSendMsgResult) {
        this.thirdAhSendMsgResult = thirdAhSendMsgResult;
    }
}
