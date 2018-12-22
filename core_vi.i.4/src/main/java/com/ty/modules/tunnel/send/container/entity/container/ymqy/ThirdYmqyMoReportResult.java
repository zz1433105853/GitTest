package com.ty.modules.tunnel.send.container.entity.container.ymqy;

import com.google.common.collect.Lists;
import com.ty.modules.tunnel.entity.MsgReply;
import com.ty.modules.msg.entity.MsgResponse;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Created by Ysw on 2016/7/4.
 */
@XmlRootElement(name="returnsms")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdYmqyMoReportResult {
    @XmlElement(name = "errorstatus", type = ThirdYmqySendReportErrorResult.class)
    private ThirdYmqySendReportErrorResult errorstatus;

    @XmlElements(value = {@XmlElement(name = "callbox", type = ThirdYmqyReportSingleResult.class)})
    private List<ThirdYmqyReportSingleResult> returnsms;
    public List<ThirdYmqyReportSingleResult> getReturnsms() {
        return returnsms;
    }

    public void setReturnsms(List<ThirdYmqyReportSingleResult> returnsms) {
        this.returnsms = returnsms;
    }

    public ThirdYmqySendReportErrorResult getErrorstatus() {
        return errorstatus;
    }

    public void setErrorstatus(ThirdYmqySendReportErrorResult errorstatus) {
        this.errorstatus = errorstatus;
    }

    public List<MsgReply> getMsgReplyList(Map<String, MsgResponse> responseMap){
        List<MsgReply> result = Lists.newArrayList();
        if(this.returnsms != null && !this.returnsms.isEmpty()){
            for(ThirdYmqyReportSingleResult thirdTxctMoReportSingleResult:this.returnsms){
                if(responseMap.get(thirdTxctMoReportSingleResult.getTaskid())==null) continue;
                MsgResponse mr = responseMap.get(thirdTxctMoReportSingleResult.getTaskid());
                MsgReply msgReply = new MsgReply();
                msgReply.setCustomer(mr.getMsgRecord().getCustomer());
                msgReply.setMobile(thirdTxctMoReportSingleResult.getMobile());
                msgReply.setContent(thirdTxctMoReportSingleResult.getContent());
                msgReply.setSrcId(mr.getSrcId());
                msgReply.setExt(mr.getMsgRecord().getExt());
                result.add(msgReply);
            }
        }
        return result;
    }

    public List<String> getAssocatedCustomerIds() {
        List<String> msgIds = Lists.newArrayList();
        for(ThirdYmqyReportSingleResult thirdTxctMoReportSingleResult:this.returnsms){
            msgIds.add(thirdTxctMoReportSingleResult.getTaskid());
        }
        return msgIds;
    }

}
