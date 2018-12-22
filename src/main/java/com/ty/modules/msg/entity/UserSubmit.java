package com.ty.modules.msg.entity;

import com.ty.common.utils.Encodes;

/**
 * SDK用户提交实体
 * Created by Ysw on 2016/6/28.
 */
public class UserSubmit {

    private String sn;
    private String password;
    private String mobile;
    private String content;
    private String contentSpecial;
    private String ext;
    private String sendTime;
    private int toFeeCount;//扣费条数
    private String isCmpp;
    private String title;
    private String msgId;
    private String srcId;

    //模板消息部分
    private String tplId;

    public UserSubmit() {
    }

    public UserSubmit(String sn, String password, String mobile, String content, String ext, String time) {
        this.sn = sn;
        this.password = password;
        this.mobile = mobile;
        this.content = content;
        this.ext = ext;
        this.sendTime = time;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getContent() {
        content = Encodes.unescapeHtml(content);
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExt() {
        return ext == null?"":ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getsendTime() {
        return sendTime==null?"":sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getToFeeCount() {
        return toFeeCount;
    }

    public void setToFeeCount(int toFeeCount) {
        this.toFeeCount = toFeeCount;
    }

    public String getContentSpecial() {
        return Encodes.unescapeHtml(contentSpecial);
    }

    public void setContentSpecial(String contentSpecial) {
        this.contentSpecial = contentSpecial;
    }

    public String getTplId() {
        return tplId;
    }

    public void setTplId(String tplId) {
        this.tplId = tplId;
    }

    public String getIsCmpp() {
        return isCmpp;
    }

    public void setIsCmpp(String isCmpp) {
        this.isCmpp = isCmpp;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
