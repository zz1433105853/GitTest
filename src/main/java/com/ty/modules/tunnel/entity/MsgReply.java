package com.ty.modules.tunnel.entity;

import com.ty.common.persistence.DataEntity;
import com.ty.modules.msg.entity.Customer;

/**
 * Created by 阿水 on 2017/4/12 09:54.
 * 上行消息
 */
public class MsgReply extends DataEntity<MsgReply> {

    private static final long serialVersionUID = 5962968269385758934L;

    private com.ty.modules.msg.entity.Customer customer;
    private String mobile;
    private String content;
    private String ext;
    private String srcId;
    private String getStatus;
    private String cSrcId;

    public com.ty.modules.msg.entity.Customer getCustomer() {
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

    public String getGetStatus() {
        return getStatus;
    }

    public void setGetStatus(String getStatus) {
        this.getStatus = getStatus;
    }

    public String getcSrcId() {
        return cSrcId;
    }

    public void setcSrcId(String cSrcId) {
        this.cSrcId = cSrcId;
    }
}
