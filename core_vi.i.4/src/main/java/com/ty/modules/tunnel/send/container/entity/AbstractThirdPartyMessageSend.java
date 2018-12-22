package com.ty.modules.tunnel.send.container.entity;

import com.ty.modules.msg.entity.MsgCxRecord;
import com.ty.modules.msg.entity.MsgRecord;

/**
 * Created by ljb on 2017/4/12 16:07.
 */
public abstract class AbstractThirdPartyMessageSend extends AbstractMessageSend{

    private static final long serialVersionUID = -3535726056418718720L;
    protected String originStr;//原始响应信息

    public AbstractThirdPartyMessageSend(MsgRecord msgRecord) {
        super(msgRecord);
    }
    public AbstractThirdPartyMessageSend(MsgCxRecord msgCxRecord) {
        super(msgCxRecord);
    }

    public String getOriginStr() {
        return originStr;
    }

    public void setOriginStr(String originStr) {
        this.originStr = originStr;
    }
}
