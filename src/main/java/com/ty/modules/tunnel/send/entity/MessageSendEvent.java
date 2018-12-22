package com.ty.modules.tunnel.send.entity;

import com.ty.modules.msg.entity.MsgRecord;

/**
 * Created by ljb on 2017/4/12 09:40.
 */
public class MessageSendEvent {

    private MsgRecord msgRecord;

    public MsgRecord getMsgRecord() {
        return msgRecord;
    }

    public void setMsgRecord(MsgRecord msgRecord) {
        this.msgRecord = msgRecord;
    }
}
