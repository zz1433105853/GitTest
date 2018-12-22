package com.ty.modules.tunnel.send.container.entity.container.hl;

import com.google.common.collect.Lists;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.MsgResponse;
import com.ty.modules.tunnel.entity.MsgReply;

import java.util.List;
import java.util.Map;

/**
 * Created by ljb on 2016/7/4.
 */
public class ThirdHlMoReportResult {
    private String ret = "";

    public ThirdHlMoReportResult(String ret) {
        this.ret = ret;
    }

    public List<MsgReply> getMsgReplyList(Map<String, MsgResponse> responseMap){
        List<MsgReply> replyLogs = Lists.newArrayList();
        if(StringUtils.isNotBlank(ret)){
            ret = ret.replace("||","/");
            for(String signle:this.ret.split("/")){
                if(StringUtils.isNotBlank(signle)){
                    String[] detail = signle.split(",");
                    MsgResponse mr = responseMap.get(detail[2]);
                    if(mr == null){
                        continue;
                    }
                    MsgReply msgReply = new MsgReply();
                    msgReply.setCustomer(mr.getMsgRecord().getCustomer());
                    msgReply.setMobile(detail[1]);
                    msgReply.setContent(detail[4]);
                    msgReply.setSrcId(mr.getSrcId());
                    msgReply.setExt(mr.getMsgRecord().getExt());
                    replyLogs.add(msgReply);
                }
            }
        }
        return replyLogs;
    }

    public List<String> getAssocatedCustomerIds() {
        List<String> msgIds = Lists.newArrayList();
        ret = ret.replace("||","/");
        for(String signl:this.ret.split("/")){
            if(StringUtils.isNotBlank(signl)){
                String taskId = signl.split(",")[2];
                msgIds.add(taskId);
            }
        }
        return msgIds;
    }

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }
}
