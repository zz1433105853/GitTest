package com.ty.modules.tunnel.send.container.entity.container.ah;

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
public class ThirdAhMoReportResult {
    @XmlElement(name = "errorstatus", type = ThirdAhSendReportErrorResult.class)
    private ThirdAhSendReportErrorResult errorstatus;

    @XmlElements(value = {@XmlElement(name = "callbox", type = ThirdAhMoReportSingleResult.class)})
    private List<ThirdAhMoReportSingleResult> returnsms;
    public List<ThirdAhMoReportSingleResult> getReturnsms() {
        return returnsms;
    }

    public void setReturnsms(List<ThirdAhMoReportSingleResult> returnsms) {
        this.returnsms = returnsms;
    }

    public ThirdAhSendReportErrorResult getErrorstatus() {
        return errorstatus;
    }

    public void setErrorstatus(ThirdAhSendReportErrorResult errorstatus) {
        this.errorstatus = errorstatus;
    }

    public List<MsgReply> getMsgReplyList(Map<String, MsgResponse> responseMap){
        List<MsgReply> replyLogs = Lists.newArrayList();
        if(this.returnsms != null){
            for(ThirdAhMoReportSingleResult thirdTxctMoReportSingleResult:this.returnsms){
                if(responseMap.get(thirdTxctMoReportSingleResult.getTaskid())==null) continue;
                MsgResponse mr = responseMap.get(thirdTxctMoReportSingleResult.getTaskid());
                MsgReply msgReply = new MsgReply();
                msgReply.setCustomer(mr.getMsgRecord().getCustomer());
                msgReply.setMobile(thirdTxctMoReportSingleResult.getMobile());
                msgReply.setContent(thirdTxctMoReportSingleResult.getContent());
                msgReply.setSrcId(mr.getSrcId());
                msgReply.setExt(mr.getMsgRecord().getExt());
                replyLogs.add(msgReply);
            }
        }
        return replyLogs;
    }

    public List<String> getAssocatedCustomerIds() {
        List<String> msgIds = Lists.newArrayList();
        for(ThirdAhMoReportSingleResult thirdTxctMoReportSingleResult:this.returnsms){
            msgIds.add(thirdTxctMoReportSingleResult.getTaskid());
        }
        return msgIds;
    }
}
