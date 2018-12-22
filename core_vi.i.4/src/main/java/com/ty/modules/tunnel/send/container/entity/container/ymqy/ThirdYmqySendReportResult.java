package com.ty.modules.tunnel.send.container.entity.container.ymqy;

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
public class ThirdYmqySendReportResult {

    @XmlElement(name = "errorstatus", type = ThirdYmqySendReportErrorResult.class)
    private ThirdYmqySendReportErrorResult errorstatus;

    @XmlElements(value = {@XmlElement(name = "statusbox", type = ThirdYmqySendReportSingleResult.class)})
    private List<ThirdYmqySendReportSingleResult> returnsms;

    public List<ThirdYmqySendReportSingleResult> getReturnsms() {
        return returnsms;
    }

    public void setReturnsms(List<ThirdYmqySendReportSingleResult> returnsms) {
        this.returnsms = returnsms;
    }

    public ThirdYmqySendReportErrorResult getErrorstatus() {
        return errorstatus;
    }

    public void setErrorstatus(ThirdYmqySendReportErrorResult errorstatus) {
        this.errorstatus = errorstatus;
    }

    public List<MsgReport> getMsgReportList(){
        List<MsgReport> result = Lists.newArrayList();
        if(this.returnsms != null){
            for(ThirdYmqySendReportSingleResult thirdTxctSendReportSingleResult:this.returnsms){
                MsgReport msgReport = new MsgReport();
                msgReport.setMsgId(thirdTxctSendReportSingleResult.getTaskid());
                msgReport.setMobile(thirdTxctSendReportSingleResult.getMobile());
                msgReport.setArrivedStatus(thirdTxctSendReportSingleResult.getReceiveStatus());
                msgReport.setArrivedResultMessage(thirdTxctSendReportSingleResult.getStatus());
                msgReport.setArrivedTime(DateUtils.parseDate(thirdTxctSendReportSingleResult.getReceivetime()));
                result.add(msgReport);
            }
        }
        return result;
    }
}
