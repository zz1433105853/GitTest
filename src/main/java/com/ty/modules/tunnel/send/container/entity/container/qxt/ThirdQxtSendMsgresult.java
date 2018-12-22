package com.ty.modules.tunnel.send.container.entity.container.qxt;

import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.tunnel.send.container.entity.AbstractThirdPartyMessageSend;

/**
 * Created by ljb on 2017/4/22 09:58.
 */
public class ThirdQxtSendMsgresult extends AbstractThirdPartyMessageSend {

    private static final long serialVersionUID = 1072180284919149165L;
    private ThirdQxt thirdQxt;
    public ThirdQxtSendMsgresult(MsgRecord msgRecord) {
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
       return null;
    }

    @Override
    public String getSeqId() {
        return "0";
    }

    @Override
    public String getResult() {
        String result = "<无效的响应>";
        if(thirdQxt != null){
            return "0".equals(thirdQxt.getCode())?"1":"2";
        }else{
            return null;
        }
    }

    @Override
    public String getResultMessage() {
        if(thirdQxt != null){
            return thirdQxt.getResult();
        }else{
            return null;
        }
    }

    public ThirdQxt getThirdQxt() {
        return thirdQxt;
    }

    public void setThirdQxt(ThirdQxt thirdQxt) {
        this.thirdQxt = thirdQxt;
    }
}
