package com.ty.modules.tunnel.response.entity;

import com.ty.modules.msg.entity.MsgResponse;

/**
 * Created by 阿水 on 2017/4/12 09:41.
 */
public class MessageResponseEvent {

    private MsgResponse msgResponse;

    public MsgResponse getMsgResponse() {
        return msgResponse;
    }

    public void setMsgResponse(MsgResponse msgResponse) {
        this.msgResponse = msgResponse;
    }
}
