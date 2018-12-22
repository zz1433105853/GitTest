package com.ty.modules.tunnel.send.container.entity;

import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.tunnel.send.container.entity.container.ymqy.ThirdYmqySendMsgResult;

/**
 * Created by 阿水 on 2017/4/12 17:08.
 */
public class ThirdMessageSendYmqy extends AbstractThirdPartyMessageSend{

    private static final long serialVersionUID = -8878600516906123776L;

    private ThirdYmqySendMsgResult thirdYmqySendMsgResult;

    public ThirdMessageSendYmqy(MsgRecord msgRecord) {
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
        if(thirdYmqySendMsgResult!=null) {
            msgId = thirdYmqySendMsgResult.getTaskID();
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
        if(thirdYmqySendMsgResult!=null) {
            result = "Success".equals(thirdYmqySendMsgResult.getReturnstatus())?"1":"2";
        }
        return result;
    }

    @Override
    public String getResultMessage() {
        String resulltOfTunnel = "<无效的响应>";
        if(thirdYmqySendMsgResult!=null) {
            resulltOfTunnel = thirdYmqySendMsgResult.getReturnstatus();
        }
        return resulltOfTunnel;
    }

    public ThirdYmqySendMsgResult getThirdYmqySendMsgResult() {
        return thirdYmqySendMsgResult;
    }

    public void setThirdYmqySendMsgResult(ThirdYmqySendMsgResult thirdYmqySendMsgResult) {
        this.thirdYmqySendMsgResult = thirdYmqySendMsgResult;
    }
}
