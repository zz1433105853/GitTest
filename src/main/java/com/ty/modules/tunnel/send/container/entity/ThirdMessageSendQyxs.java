package com.ty.modules.tunnel.send.container.entity;

import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.tunnel.send.container.entity.container.qyxs.ThirdQyxsSendMsgResult;

/**
 * Created by zz on 2019/1/8
 */
public class ThirdMessageSendQyxs extends AbstractThirdPartyMessageSend{

    private static final long serialVersionUID = -8878600516906123776L;

    private ThirdQyxsSendMsgResult thirdQyxsSendMsgResult;

    public ThirdMessageSendQyxs(MsgRecord msgRecord) {
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
        if(thirdQyxsSendMsgResult!=null) {
            msgId = thirdQyxsSendMsgResult.getTaskID();
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
        if(thirdQyxsSendMsgResult!=null) {
            result = "Success".equals(thirdQyxsSendMsgResult.getReturnstatus())?"1":"2";
        }
        return result;
    }

    @Override
    public String getResultMessage() {
        String resulltOfTunnel = "<无效的响应>";
        if(thirdQyxsSendMsgResult!=null) {
            resulltOfTunnel = thirdQyxsSendMsgResult.getReturnstatus();
        }
        return resulltOfTunnel;
    }

    public ThirdQyxsSendMsgResult getThirdQyxsSendMsgResult() {
        return thirdQyxsSendMsgResult;
    }

    public void setThirdQyxsSendMsgResult(ThirdQyxsSendMsgResult thirdQyxsSendMsgResult) {
        this.thirdQyxsSendMsgResult = thirdQyxsSendMsgResult;
    }
}
