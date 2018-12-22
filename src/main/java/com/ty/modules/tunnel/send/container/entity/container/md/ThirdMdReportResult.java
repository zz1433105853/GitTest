package com.ty.modules.tunnel.send.container.entity.container.md;

import com.google.common.collect.Lists;
import com.ty.common.utils.DateUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.MsgReport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.List;

/**
 * Created by tykfkf02 on 2016/9/3.
 */

@XmlRootElement(name="string", namespace="http://tempuri.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdMdReportResult {
    @XmlValue
    private String report;

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public List<MsgReport> getMsgReportList(){
        List<MsgReport> result = Lists.newArrayList();
        if(StringUtils.isNotBlank(report)){
            String[] reportCount = report.split("\n");
            for(String report:reportCount){
                if(StringUtils.isNotBlank(report)){
                    String reportDetail[] = report.split(",");
                    if(reportDetail.length == 6){
                        MsgReport msgReport = new MsgReport();
                        msgReport.setMobile((reportDetail[2]));
                        msgReport.setMsgId(reportDetail[3]);
                        msgReport.setArrivedStatus(getArrivedStatus(reportDetail[4]));
                        msgReport.setArrivedTime(DateUtils.parseDate(reportDetail[5]));
                        result.add(msgReport);
                    }
                }
            }
        }
        return result;
    }

    private String getArrivedStatus(String reportStatus){
        if(StringUtils.isNotBlank(reportStatus) && ("DELIVRD".equals(reportStatus) || "0".equals(reportStatus))){
            return "1";
        }else{
            return "2";
        }
    }
}
