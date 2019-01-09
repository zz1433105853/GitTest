package com.ty.modules.tunnel.send.container.entity.container.tykj;

/**
 * 上行报告
 * Created by tykfkf02 on 2016/9/26.
 */
public class ThirdTykjReplySimple {
    private String ext;
    private String mobile;
    private String content;
    private String replyTime;
    //后加的srcId
    private String srcId;

    public ThirdTykjReplySimple() {
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
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

    public void setReplyTime(String arrivedTime) {
        this.replyTime = arrivedTime;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
