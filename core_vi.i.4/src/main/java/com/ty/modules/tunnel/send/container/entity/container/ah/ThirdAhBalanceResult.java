package com.ty.modules.tunnel.send.container.entity.container.ah;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Ysw on 2016/7/2.
 */
@XmlRootElement(name="returnsms")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdAhBalanceResult {

    private String returnstatus;
    private String message;
    private long overage;
    private String payinfo;
    private long sendTotal;

    public ThirdAhBalanceResult() {
    }

    public ThirdAhBalanceResult(String returnstatus, String message, long overage, String payinfo, long sendTotal) {
        this.returnstatus = returnstatus;
        this.message = message;
        this.overage = overage;
        this.payinfo = payinfo;
        this.sendTotal = sendTotal;
    }

    public String getReturnstatus() {
        return returnstatus;
    }

    public void setReturnstatus(String returnstatus) {
        this.returnstatus = returnstatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getOverage() {
        return overage;
    }

    public void setOverage(long overage) {
        this.overage = overage;
    }

    public String getPayinfo() {
        return payinfo;
    }

    public void setPayinfo(String payinfo) {
        this.payinfo = payinfo;
    }

    public long getSendTotal() {
        return sendTotal;
    }

    public void setSendTotal(long sendTotal) {
        this.sendTotal = sendTotal;
    }
}
