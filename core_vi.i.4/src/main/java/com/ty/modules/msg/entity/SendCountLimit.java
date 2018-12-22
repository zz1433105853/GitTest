package com.ty.modules.msg.entity;

/**
 * Created by Ysw on 2016/12/28.
 */
public class SendCountLimit {

    private int minuteSendCountLimit;
    private int hourSendCountLimit;
    private int daySendCountLimit;

    public SendCountLimit() {
    }

    public SendCountLimit(int minuteSendCountLimit, int hourSendCountLimit, int daySendCountLimit) {
        this.minuteSendCountLimit = minuteSendCountLimit;
        this.hourSendCountLimit = hourSendCountLimit;
        this.daySendCountLimit = daySendCountLimit;
    }

    public int getMinuteSendCountLimit() {
        return minuteSendCountLimit;
    }

    public void setMinuteSendCountLimit(int minuteSendCountLimit) {
        this.minuteSendCountLimit = minuteSendCountLimit;
    }

    public int getHourSendCountLimit() {
        return hourSendCountLimit;
    }

    public void setHourSendCountLimit(int hourSendCountLimit) {
        this.hourSendCountLimit = hourSendCountLimit;
    }

    public int getDaySendCountLimit() {
        return daySendCountLimit;
    }

    public void setDaySendCountLimit(int daySendCountLimit) {
        this.daySendCountLimit = daySendCountLimit;
    }
}
