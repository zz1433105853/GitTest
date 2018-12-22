package com.ty.modules.tunnel.send.container.entity;

import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.tunnel.send.container.entity.AbstractThirdPartyMessageSend;
import com.ty.modules.tunnel.send.container.entity.container.klws.ThirdKlwsResultCode;

/**
 * Created by 阿水 on 2017/4/12 17:10.
 */
public class ThirdMessageSendklws extends AbstractThirdPartyMessageSend {

    private static final long serialVersionUID = -9039217796569464649L;
    private ThirdKlwsResultCode thirdKlwsResultCode;

    public ThirdMessageSendklws(MsgRecord msgRecord) {
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
        if(thirdKlwsResultCode!=null) {
            msgId = thirdKlwsResultCode.getTaskid();
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
        if(thirdKlwsResultCode!=null) {
            result = "0".equals(thirdKlwsResultCode.getStatus())?"1":"2";
        }
        return result;
    }

    @Override
    public String getResultMessage() {
        String resulltOfTunnel = "<无效的响应>";
        if(thirdKlwsResultCode!=null) {
            resulltOfTunnel = thirdKlwsResultCode.getStatus();
        }
        return resulltOfTunnel;
    }

    public ThirdKlwsResultCode getThirdKlwsResultCode() {
        return thirdKlwsResultCode;
    }

    public void setThirdKlwsResultCode(ThirdKlwsResultCode thirdKlwsResultCode) {
        this.thirdKlwsResultCode = thirdKlwsResultCode;
    }
}
