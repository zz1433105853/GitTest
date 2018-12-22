package com.ty.modules.tunnel.send.container.entity.container.hl;

import com.google.common.collect.Lists;
import com.ty.common.utils.DateUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.MsgReport;
import com.ty.modules.tunnel.send.container.entity.container.ah.ThirdAhSendReportErrorResult;
import com.ty.modules.tunnel.send.container.entity.container.ah.ThirdAhSendReportSingleResult;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * 鸿联
 * Created by ljb on 2016/7/4.
 */
public class ThirdHlSendReportResult {
    private String result="";

    public ThirdHlSendReportResult(String result) {
        this.result = result;
    }

    public List<MsgReport> getMsgReportList(){
        List<MsgReport> msgReportList = Lists.newArrayList();
        if(StringUtils.isNotBlank(this.result)){
            result = result.replace("||","/");
            for(String signle:this.result.split("/")){
                if(StringUtils.isNotBlank(signle)){
                    MsgReport mr = new MsgReport();
                    String[] detail = signle.split(",");
                    mr.setMsgId(detail[2]);
                    mr.setMobile(detail[1]);
                    mr.setArrivedStatus(StringUtils.inString(detail[3],"0","DELIVRD")?"1":"2");
                    mr.setArrivedResultMessage(detail[3]);
                    mr.setArrivedTime(DateUtils.parseDate(detail[4]));
                    msgReportList.add(mr);
                }
            }

        }
        return msgReportList;
    }
}
