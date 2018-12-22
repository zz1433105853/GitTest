package com.ty.modules.tunnel.send.container.entity.container.tykj;

import com.google.common.collect.Lists;
import com.ty.common.utils.DateUtils;

import com.ty.modules.msg.entity.MsgReport;
import org.apache.log4j.Logger;

import java.util.List;

public class ThirdTykjReportResult {
    private static Logger logger= Logger.getLogger(ThirdTykjReportResult.class);


    private List<ThirdTykjReportSimple> data;
    public List<ThirdTykjReportSimple> getData() {
        return data;
    }
    public void setData(List<ThirdTykjReportSimple> data)
    { this.data=data; }



    private Status status;
    public Status  getStatus()
    {return status;}
    public void setStatus(Status status)
    { this.status=status; }





    public List<MsgReport> getMsgReportList(){
        List<MsgReport> result = Lists.newArrayList();
        if(this.getData()!=null){
            for(ThirdTykjReportSimple thirdTykjReportSimple:this.getData()){
                MsgReport msgReport = new MsgReport();
                msgReport.setMsgId(thirdTykjReportSimple.getTaskid());
                msgReport.setMobile(thirdTykjReportSimple.getMobile());
/*                    msgReport.setArrivedStatus(arrivedStatusReport.getArrivedStatus()=="0"?"1":"0");
                    msgReport.setArrivedResultMessage((arrivedStatusReport.getArrivedStatus()=="1")?"DELIVRD":"NO_DELIVRD");*/
                msgReport.setArrivedStatus("1");
                msgReport.setArrivedResultMessage("DELIVRD");
                msgReport.setArrivedTime(DateUtils.parseDate(thirdTykjReportSimple.getArrivedTime()));
                result.add(msgReport);

            }
        }
        return result;
    }

}
