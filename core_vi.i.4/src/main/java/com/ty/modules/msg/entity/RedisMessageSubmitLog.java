package com.ty.modules.msg.entity;

import com.ty.common.persistence.DataEntity;
import com.ty.common.utils.StringUtils;

import java.util.Date;

/**
 * Created by Ysw on 2016/6/21.
 * 消息提交记录
 */
public class RedisMessageSubmitLog extends DataEntity<RedisMessageSubmitLog> {
    private static final long serialVersionUID = 8465102796167983332L;
    private String customerId;
    private String mobile;
    private String content;
    private String isTimeing;
    private Date sendTime;
    private String fileUrl;
    private String status;
    private String cSrdId;
    private String extCode;
    private String ip;
    private String statusInfo;
    private int toFeeCount;
    private String auditBy;
    private String contentSpecial;

    public RedisMessageSubmitLog() {
    }

    public RedisMessageSubmitLog(String id) {
        super(id);
    }

    public RedisMessageSubmitLog(String id,String customerId, String mobile, String content,
                                 String isTimeing, Date sendTime, String fileUrl, String status, String cSrdId,
                                 String extCode, String ip, String statusInfo, int toFeeCount, String auditBy,
                                 String contentSpecial,Date createDate,Date updateDate, String remarks ) {
        this.id = id;
        this.customerId = customerId;
        this.mobile = mobile;
        this.content = content;
        this.isTimeing = isTimeing;
        this.sendTime = sendTime;
        this.fileUrl = fileUrl;
        this.status = status;
        this.cSrdId = cSrdId;
        this.extCode = extCode;
        this.ip = ip;
        this.statusInfo = statusInfo;
        this.toFeeCount = toFeeCount;
        this.auditBy = auditBy;
        this.contentSpecial = contentSpecial;
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

    public String getcSrdId() {
        return cSrdId;
    }

    public void setcSrdId(String cSrdId) {
        this.cSrdId = cSrdId;
    }
}
