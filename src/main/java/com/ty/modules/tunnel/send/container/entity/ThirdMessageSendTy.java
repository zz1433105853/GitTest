package com.ty.modules.tunnel.send.container.entity;

import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.tunnel.send.container.entity.container.tykj.ResultCode;

/**
 * Created by 阿水 on 2017/4/12 17:10.
 */
public class ThirdMessageSendTy extends AbstractThirdPartyMessageSend {

    private static final long serialVersionUID = -9039217796569464649L;
    private ResultCode resultCode;//天源科技

    public ThirdMessageSendTy(MsgRecord msgRecord) {
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
        if(resultCode!=null) {
            msgId = resultCode.getTaskid();
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
        if(resultCode!=null) {
            result = "0".equals(resultCode.getStatus().getCode())?"1":"2";
        }
        return result;
    }

    @Override
    public String getResultMessage() {
        String resulltOfTunnel = "<无效的响应>";
        if(resultCode!=null) {
            resulltOfTunnel = resultCode.getStatus().getCode();
        }
        return resulltOfTunnel;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
