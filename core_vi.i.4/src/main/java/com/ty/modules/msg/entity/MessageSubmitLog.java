package com.ty.modules.msg.entity;

import com.ty.common.persistence.DataEntity;
import com.ty.common.utils.StringUtils;

import java.util.Date;

/**
 * Created by Ysw on 2016/6/21.
 * 消息提交记录
 */
public class MessageSubmitLog extends DataEntity<MessageSubmitLog> {
    private static final long serialVersionUID = 18752240418937503L;
    private Customer customer;
    private String mobile;
    private String content;
    private String isTimeing;
    private Date sendTime;
    private String fileUrl;
    private String status;
    private String cSrcId;
    private String extCode;
    private String ip;
    private String statusInfo;
    private int toFeeCount;
    private String auditBy;
    private String contentSpecial;

    public MessageSubmitLog() {
    }

    public MessageSubmitLog(String id) {
        super(id);
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

    public String getIsTimeing() {
        return isTimeing;
    }

    public void setIsTimeing(String isTimeing) {
        this.isTimeing = isTimeing;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getToFeeCount() {
        return toFeeCount;
    }

    public void setToFeeCount(int toFeeCount) {
        this.toFeeCount = toFeeCount;
    }

    /**
     * 获取用户提交信息的字数
     * @return
     */
    public int getContentWordCount() {
        int result = 0;
        if(StringUtils.isNotEmpty(this.content)) {
            result = this.content.length();
        }
        return result;
    }

    /**
     * 获取用户提交的短信请求的手机号码数量
     * @return
     */
    public int getMobileCount() {
        int result = 0;
        if(StringUtils.isNotEmpty(this.mobile)) {
            result  = this.mobile.split(",").length;
        }
        return result;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public String getAuditBy() {
        return auditBy;
    }

    public void setAuditBy(String auditBy) {
        this.auditBy = auditBy;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getContentSpecial() {
        return contentSpecial;
    }

    public void setContentSpecial(String contentSpecial) {
        this.contentSpecial = contentSpecial;
    }

    public String getcSrcId() {
        return cSrcId;
    }

    public void setcSrcId(String cSrcId) {
        this.cSrcId = cSrcId;
    }
}
