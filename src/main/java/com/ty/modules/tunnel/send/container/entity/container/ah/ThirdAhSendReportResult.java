package com.ty.modules.tunnel.send.container.entity.container.ah;

import com.google.common.collect.Lists;
import com.ty.common.utils.DateUtils;
import com.ty.modules.msg.entity.MsgReport;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by Ysw on 2016/7/4.
 */
@XmlRootElement(name="returnsms")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdAhSendReportResult {

    @XmlElement(name = "errorstatus", type = ThirdAhSendReportErrorResult.class)
    private ThirdAhSendReportErrorResult errorstatus;

    @XmlElements(value = {@XmlElement(name = "statusbox", type = ThirdAhSendReportSingleResult.class)})
    private List<ThirdAhSendReportSingleResult> returnsms;

    public List<ThirdAhSendReportSingleResult> getReturnsms() {
        return returnsms;
    }

    public void setReturnsms(List<ThirdAhSendReportSingleResult> returnsms) {
        this.returnsms = returnsms;
    }

    public ThirdAhSendReportErrorResult getErrorstatus() {
        return errorstatus;
    }

    public void setErrorstatus(ThirdAhSendReportErrorResult errorstatus) {
        this.errorstatus = errorstatus;
    }

    public List<MsgReport> getMsgReportList(){
        List<MsgReport> msgReportList = Lists.newArrayList();
        if(this.returnsms != null){
            for(ThirdAhSendReportSingleResult thirdTxctSendReportSingleResult:this.returnsms){
                MsgReport mr = new MsgReport();
                mr.setMsgId(thirdTxctSendReportSingleResult.getTaskid());
                mr.setMobile(thirdTxctSendReportSingleResult.getMobile());
                mr.setArrivedStatus(thirdTxctSendReportSingleResult.getReceiveStatus());
                mr.setArrivedResultMessage(thirdTxctSendReportSingleResult.getStatus());
                mr.setArrivedTime(DateUtils.parseDate(thirdTxctSendReportSingleResult.getReceivetime()));
                msgReportList.add(mr);
            }
        }
        return msgReportList;
    }
}
