package com.ty.modules.tunnel.send.container.entity;

import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.MsgRecord;

/**
 * Created by ljb on 2017/4/22 09:58.
 */
public class ThirdMessageSendHl extends AbstractThirdPartyMessageSend {

    private static final long serialVersionUID = 1072180284919149165L;
    private String msgId = "";
    private String res = "";
    public ThirdMessageSendHl(MsgRecord msgRecord) {
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
       return msgId;
    }

    @Override
    public String getSeqId() {
        return "0";
    }

    @Override
    public String getResult() {
        String result = "<无效的响应>";
        if(StringUtils.isNotBlank(res)) {
            result = "00".equals(res)?"1":"2";
        }
        return result;
    }

    @Override
    public String getResultMessage() {
        return res;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }
}
