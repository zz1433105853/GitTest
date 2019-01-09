package com.ty.modules.tunnel.send.container.entity.container.qyxs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by zz on 2019/1/8
 */
@XmlRootElement(name="returnsms")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdQyxsBalanceResult {

    private String returnstatus;//返回状态值
    private String message;//返回信息提示
    private long overage;//返回余额
    private String payinfo;//返回支付方式
    private long sendTotal;//返回总充值点数

    public ThirdQyxsBalanceResult() {
    }

    public ThirdQyxsBalanceResult(String returnstatus, String message, long overage, String payinfo, long sendTotal) {
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
