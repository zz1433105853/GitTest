package com.ty.modules.tunnel.send.container.entity.container.qyxs;

import com.google.common.collect.Lists;
import com.ty.common.utils.DateUtils;
import com.ty.modules.msg.entity.MsgReport;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by Ysw on 2019/1/8
 */
@XmlRootElement(name="returnsms")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdQyxsSendReportResult {

    @XmlElement(name = "errorstatus", type = ThirdQyxsSendReportErrorResult.class)
    private ThirdQyxsSendReportErrorResult errorstatus;

    @XmlElements(value = {@XmlElement(name = "statusbox", type = ThirdQyxsSendReportSingleResult.class)})
    private List<ThirdQyxsSendReportSingleResult> returnsms;

    public List<ThirdQyxsSendReportSingleResult> getReturnsms() {
        return returnsms;
    }

    public void setReturnsms(List<ThirdQyxsSendReportSingleResult> returnsms) {
        this.returnsms = returnsms;
    }

    public ThirdQyxsSendReportErrorResult getErrorstatus() {
        return errorstatus;
    }

    public void setErrorstatus(ThirdQyxsSendReportErrorResult errorstatus) {
        this.errorstatus = errorstatus;
    }

    public List<MsgReport> getMsgReportList(){
        List<MsgReport> result = Lists.newArrayList();
        if(this.returnsms != null){
            for(ThirdQyxsSendReportSingleResult thirdQyxsSendReportSingleResult:this.returnsms){
                MsgReport msgReport = new MsgReport();
                msgReport.setMsgId(thirdQyxsSendReportSingleResult.getTaskid());
                msgReport.setMobile(thirdQyxsSendReportSingleResult.getMobile());
                msgReport.setArrivedStatus(thirdQyxsSendReportSingleResult.getReceiveStatus());
                msgReport.setArrivedResultMessage(thirdQyxsSendReportSingleResult.getStatus());
                msgReport.setArrivedTime(DateUtils.parseDate(thirdQyxsSendReportSingleResult.getReceivetime()));
                result.add(msgReport);
            }
        }
        return result;
    }
}
