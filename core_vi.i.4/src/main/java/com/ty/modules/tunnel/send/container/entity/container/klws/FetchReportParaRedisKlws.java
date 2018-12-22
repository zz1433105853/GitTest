package com.ty.modules.tunnel.send.container.entity.container.klws;

import java.io.Serializable;

public class FetchReportParaRedisKlws implements Serializable {
    private static final long serialVersionUID = 6035798783237284598L;

    private String msgid;
    private String mobile;
    private String stat;
    private String arrivedTime;

    public FetchReportParaRedisKlws() {

    }
    public FetchReportParaRedisKlws(String msgid, String mobile, String stat, String arrivedTime) {
        this.msgid= msgid;
        this.mobile = mobile;
        this.stat=stat;
        this.arrivedTime=arrivedTime;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }
    public String getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(String arrivedTime) {
        this.arrivedTime = arrivedTime;
    }




    // String todayTime = DateUtils.getDate(0);
    // String yeTime=DateUtils.getDate(1);
    //  List<MsgReport> findNeedReportList=smsSendLogService.findNeedReportListByNum(100,todayTime,yeTime);



}
