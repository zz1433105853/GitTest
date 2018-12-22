package com.ty.modules.msg.entity;

/**
 * Created by Ysw on 2016/9/24.
 */
public class SpecialMsgVo {

    private String mobile;
    private String content;
    private String sendTime;

    public SpecialMsgVo() {
    }

    public SpecialMsgVo(String mobile, String content, String sendTime) {
        this.mobile = mobile;
        this.content = content;
        this.sendTime = sendTime;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
