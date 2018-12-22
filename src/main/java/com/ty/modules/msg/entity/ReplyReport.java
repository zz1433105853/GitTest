package com.ty.modules.msg.entity;

/**
 * Created by Ysw on 2016/9/12.
 */
public class ReplyReport{
    private String mobile;
    private String ext;
    private String content;
    private String replyTime;

    public ReplyReport() {
    }


    public ReplyReport(String ext,String mobile, String content,String replyTime) {
        this.ext = ext;
        this.mobile = mobile;
        this.content = content;
        this.replyTime = replyTime;
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

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
