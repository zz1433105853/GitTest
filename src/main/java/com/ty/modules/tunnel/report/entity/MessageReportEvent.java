package com.ty.modules.tunnel.report.entity;

import com.ty.modules.msg.entity.MsgReport;

/**
 * Created by ljb on 2017/4/12 09:41.
 */
public class MessageReportEvent {

    private MsgReport msgReport;

    public MsgReport getMsgReport() {
        return msgReport;
    }

    public void setMsgReport(MsgReport msgReport) {
        this.msgReport = msgReport;
    }
}
