package com.ty.modules.msg.entity;

import com.ty.common.persistence.DataEntity;
import com.ty.common.utils.DateUtils;
import com.ty.common.utils.Encodes;

import java.util.Date;
import java.util.List;

/**
 * 封装发送ringBuffer
 * Created by tykfkf02 on 2016/6/30.
 */
public class CxAuditSend extends DataEntity<CxAuditSend> {
    private String mobile;
    private String content;
    private Customer customer;
    private UserSubmit userSubmit;
    private MessageCxSubmitLog messageCxSubmitLog;
    private String srcId;
    private String containerId;
    private Date intoWindowMapTime;

    private List<MsgRecord> reMsgRecordList;//重发list
    private String containerIsp1Id;
    private String containerIsp2Id;
    private String containerIsp3Id;


    //单独
    private MsgRecord msgRecord;
    //打包发送，一般为第三方接口
    private List<MsgRecord> msgRecordList;

    private String originStr;

    //彩信部分
    private String url;
    private String desc;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getContent() {
        content  = Encodes.unescapeHtml(content);
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserSubmit getUserSubmit() {
        if(userSubmit==null) userSubmit = new UserSubmit();
        return userSubmit;
    }

    public void setUserSubmit(UserSubmit userSubmit) {
        this.userSubmit = userSubmit;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public MessageCxSubmitLog getMessageCxSubmitLog() {
        return messageCxSubmitLog;
    }

    public void setMessageCxSubmitLog(MessageCxSubmitLog messageCxSubmitLog) {
        this.messageCxSubmitLog = messageCxSubmitLog;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getOriginStr() {
        return originStr;
    }

    public void setOriginStr(String originStr) {
        this.originStr = originStr;
    }

    public Date getIntoWindowMapTime() {
        return intoWindowMapTime;
    }

    public void setIntoWindowMapTime(Date intoWindowMapTime) {
        this.intoWindowMapTime = intoWindowMapTime;
    }

    /**
     * 打印该消息在WindowMap中存在的时间长短
     * @return
     */
    public String getMessageSendLiveInWindowMapTime() {
        StringBuilder result = new StringBuilder();
        if(intoWindowMapTime!=null) {
            long ml = System.currentTimeMillis()-intoWindowMapTime.getTime();
            result.append("进入时间："+ DateUtils.formatDateTime(intoWindowMapTime)+" 已经待了："+DateUtils.formatDateTime(ml)+ "\r\n");
        }
        return result.toString();
    }
    public String getContainerIsp1Id() {
        return containerIsp1Id;
    }

    public void setContainerIsp1Id(String containerIsp1Id) {
        this.containerIsp1Id = containerIsp1Id;
    }

    public String getContainerIsp2Id() {
        return containerIsp2Id;
    }

    public void setContainerIsp2Id(String containerIsp2Id) {
        this.containerIsp2Id = containerIsp2Id;
    }

    public String getContainerIsp3Id() {
        return containerIsp3Id;
    }

    public void setContainerIsp3Id(String containerIsp3Id) {
        this.containerIsp3Id = containerIsp3Id;
    }

    public List<MsgRecord> getReMsgRecordList() {
        return reMsgRecordList;
    }

    public void setReMsgRecordList(List<MsgRecord> reMsgRecordList) {
        this.reMsgRecordList = reMsgRecordList;
    }

    public MsgRecord getMsgRecord() {
        return msgRecord;
    }

    public void setMsgRecord(MsgRecord msgRecord) {
        this.msgRecord = msgRecord;
    }

    public List<MsgRecord> getMsgRecordList() {
        return msgRecordList;
    }

    public void setMsgRecordList(List<MsgRecord> msgRecordList) {
        this.msgRecordList = msgRecordList;
    }
}
