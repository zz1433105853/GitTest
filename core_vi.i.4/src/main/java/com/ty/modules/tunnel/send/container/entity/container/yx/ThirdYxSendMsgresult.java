package com.ty.modules.tunnel.send.container.entity.container.yx;

import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.tunnel.send.container.entity.AbstractThirdPartyMessageSend;

/**
 * Created by ljb on 2017/4/22 09:58.
 */
public class ThirdYxSendMsgresult extends AbstractThirdPartyMessageSend {

    private static final long serialVersionUID = 1072180284919149165L;
    private String res = "";
    public ThirdYxSendMsgresult(MsgRecord msgRecord) {
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
        if(StringUtils.isNotBlank(res)){
            if(res.contains("&")){
                String[] result = res.split("&");
                return result[1].replace("id=","");
            }else if(res.contains("id")){
                return res.replace("id=","");
            }else{
                return null;
            }
        }
        return null;
    }

    @Override
    public String getSeqId() {
        return "0";
    }

    @Override
    public String getResult() {
        String result = "<无效的响应>";
        if(StringUtils.isNotBlank(res) && res.contains("id")) {
            return "1";
        }else{
            return "2";
        }
    }

    @Override
    public String getResultMessage() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }
}
