package com.ty.modules.msg.entity;

import com.ty.common.persistence.DataEntity;

/**
 * Created by Ysw on 2016/9/12.
 */
public class ReplyLog extends DataEntity<ReplyLog> {

    private Customer customer;
    private String mobile;
    private String content;
    private String status;
    private String getStatus;
    private String srcId;
    private String ext;

    public ReplyLog() {
    }

    public ReplyLog(String id) {
        super(id);
    }


    public ReplyLog(Customer customer, String mobile, String content) {
        this.customer = customer;
        this.mobile = mobile;
        this.content = content;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGetStatus() {
        return getStatus;
    }

    public void setGetStatus(String getStatus) {
        this.getStatus = getStatus;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }
}
