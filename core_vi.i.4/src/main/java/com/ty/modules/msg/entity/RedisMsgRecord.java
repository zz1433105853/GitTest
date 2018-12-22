package com.ty.modules.msg.entity;

import com.ty.common.persistence.DataEntity;

import java.util.Date;

/**
 * Created by tykfkf02 on 2017/4/12.
 */
public class RedisMsgRecord extends DataEntity<RedisMsgRecord> {
    private static final long serialVersionUID = 3090712593952791580L;
    private String customerId;
    private String mobile;
    private String content;
    private int contentPayCount;
    private String refuseSendMessage;//字典数据
    private String submitLogId;
    private String sequenceNumber;//序号
    private Date startDate;
    private Date endDate;
    private String ext;
    private String cSrcId;//cmpp客户srcid

    public RedisMsgRecord() {
    }

    public RedisMsgRecord(String id, String customerId, String mobile, String content,
                          int contentPayCount, String refuseSendMessage, String submitLogId,
                          String sequenceNumber, Date startDate, Date endDate, String ext,
                          String cSrcId,Date createDate,Date updateDate, String remarks) {
        this.id = id;
        this.customerId = customerId;
        this.mobile = mobile;
        this.content = content;
        this.contentPayCount = contentPayCount;
        this.refuseSendMessage = refuseSendMessage;
        this.submitLogId = submitLogId;
        this.sequenceNumber = sequenceNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ext = ext;
        this.cSrcId = cSrcId;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.remarks = remarks;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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

    public int getContentPayCount() {
        return contentPayCount;
    }

    public void setContentPayCount(int contentPayCount) {
        this.contentPayCount = contentPayCount;
    }

    public String getRefuseSendMessage() {
        return refuseSendMessage;
    }

    public void setRefuseSendMessage(String refuseSendMessage) {
        this.refuseSendMessage = refuseSendMessage;
    }

    public String getSubmitLogId() {
        return submitLogId;
    }

    public void setSubmitLogId(String submitLogId) {
        this.submitLogId = submitLogId;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getcSrcId() {
        return cSrcId;
    }

    public void setcSrcId(String cSrcId) {
        this.cSrcId = cSrcId;
    }
}
