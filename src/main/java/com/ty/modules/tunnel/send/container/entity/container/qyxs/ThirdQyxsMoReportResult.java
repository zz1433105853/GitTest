package com.ty.modules.tunnel.send.container.entity.container.qyxs;

import com.google.common.collect.Lists;
import com.ty.modules.msg.entity.MsgResponse;
import com.ty.modules.tunnel.entity.MsgReply;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Created by Ysw on 2016/7/4.
 */
@XmlRootElement(name="returnsms")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdQyxsMoReportResult {
    @XmlElement(name = "errorstatus", type = ThirdQyxsSendReportErrorResult.class)
    private ThirdQyxsSendReportErrorResult errorstatus;

    @XmlElements(value = {@XmlElement(name = "callbox", type = ThirdQyxsReportSingleResult.class)})
    private List<ThirdQyxsReportSingleResult> returnsms;
    public List<ThirdQyxsReportSingleResult> getReturnsms() {
        return returnsms;
    }

    public void setReturnsms(List<ThirdQyxsReportSingleResult> returnsms) {
        this.returnsms = returnsms;
    }

    public ThirdQyxsSendReportErrorResult getErrorstatus() {
        return errorstatus;
    }

    public void setErrorstatus(ThirdQyxsSendReportErrorResult errorstatus) {
        this.errorstatus = errorstatus;
    }

    public List<MsgReply> getMsgReplyList(Map<String, MsgResponse> responseMap){
        List<MsgReply> result = Lists.newArrayList();
        if(this.returnsms != null && !this.returnsms.isEmpty()){
            for(ThirdQyxsReportSingleResult thirdQyxsMoReportSingleResult:this.returnsms){
                if(responseMap.get(thirdQyxsMoReportSingleResult.getTaskid())==null) continue;
                MsgResponse mr = responseMap.get(thirdQyxsMoReportSingleResult.getTaskid());
                MsgReply msgReply = new MsgReply();
                msgReply.setCustomer(mr.getMsgRecord().getCustomer());
                msgReply.setMobile(thirdQyxsMoReportSingleResult.getMobile());
                msgReply.setContent(thirdQyxsMoReportSingleResult.getContent());
                msgReply.setSrcId(mr.getSrcId());
                msgReply.setExt(mr.getMsgRecord().getExt());
                result.add(msgReply);
            }
        }
        return result;
    }

    public List<String> getAssocatedCustomerIds() {
        List<String> msgIds = Lists.newArrayList();
        for(ThirdQyxsReportSingleResult thirdQyxsReportSingleResult:this.returnsms){
            msgIds.add(thirdQyxsReportSingleResult.getTaskid());
        }
        return msgIds;
    }

}
