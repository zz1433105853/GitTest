package com.ty.modules.msg.entity;

import com.google.common.collect.Lists;
import com.ty.common.persistence.DataEntity;

import java.util.Date;
import java.util.List;

/**
 * Created by tykfkf02 on 2017/4/12.
 */
public class MsgCxRecord extends DataEntity<MsgCxRecord> {
    private static final long serialVersionUID = 798469076165908174L;
    private Customer customer;
    private String mobile;
    private String title;
    private String content;
    private int contentPayCount;
    private String refuseSendMessage;//字典数据
    private MessageCxSubmitLog messageCxSubmitLog;
    private String sequenceNumber;//序号
    private Date startDate;
    private Date endDate;
    private String ext;
    private MsgResponse msgResponse;
    private MsgReport msgReport;
    private List<MsgCxRecord> msgRecordList = Lists.newArrayList();
    private String isp1;
    private String isp2;
    private String isp3;
    private String cSrcId;//cmpp客户srcid

    public MsgCxRecord() {
    }

    public MsgCxRecord(String id) {
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

    public MessageCxSubmitLog getMessageCxSubmitLog() {
        return messageCxSubmitLog;
    }

    public void setMessageCxSubmitLog(MessageCxSubmitLog messageCxSubmitLog) {
        this.messageCxSubmitLog = messageCxSubmitLog;
    }

    public int getContentPayCount() {
        return contentPayCount;
    }

    public void setContentPayCount(int contentPayCount) {
        this.contentPayCount = contentPayCount;
    }

    public int getContentPayCount(String content) {
        int contentCount = 0;
        if (content.length() <= 70) {
            contentCount = 1;
        } else {
            contentCount = content.length() / 67;
            if (content.length() % 67 != 0) {
                contentCount += 1;
            }
        }
        return contentCount;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getRefuseSendMessage() {
        return refuseSendMessage;
    }

    public void setRefuseSendMessage(String refuseSendMessage) {
        this.refuseSendMessage = refuseSendMessage;
    }

    public MsgResponse getMsgResponse() {
        return msgResponse;
    }

    public void setMsgResponse(MsgResponse msgResponse) {
        this.msgResponse = msgResponse;
    }

    public MsgReport getMsgReport() {
        return msgReport;
    }

    public void setMsgReport(MsgReport msgReport) {
        this.msgReport = msgReport;
    }

    public List<MsgCxRecord> getMsgRecordList() {
        return msgRecordList;
    }

    public void setMsgRecordList(List<MsgCxRecord> msgRecordList) {
        this.msgRecordList = msgRecordList;
    }

    public String getIsp1() {
        return isp1;
    }

    public void setIsp1(String isp1) {
        this.isp1 = isp1;
    }

    public String getIsp2() {
        return isp2;
    }

    public void setIsp2(String isp2) {
        this.isp2 = isp2;
    }

    public String getIsp3() {
        return isp3;
    }

    public void setIsp3(String isp3) {
        this.isp3 = isp3;
    }

    public String getcSrcId() {
        return cSrcId;
    }

    public void setcSrcId(String cSrcId) {
        this.cSrcId = cSrcId;
    }

    public static MsgCxRecord buildFromMsg(Msg msg) {
        MsgCxRecord result = new MsgCxRecord();
        result.setId(msg.getMsgRecordId());
        result.setCustomer(new Customer(msg.getCustomerId()));
        result.setMobile(msg.getMobile());
        result.setTitle(msg.getTitle());
        result.setContent(msg.getContent());
        result.setExt(msg.getExt());
        result.setIsp1(msg.getIsp1());
        result.setIsp2(msg.getIsp2());
        result.setIsp3(msg.getIsp3());
        if(msg.getMsgList()!=null && !msg.getMsgList().isEmpty()) {
            List<MsgCxRecord> mrList = Lists.newArrayList();
            for(Msg m : msg.getMsgList()) {
                mrList.add(buildFromMsg(m));
            }
            result.setMsgRecordList(mrList);
        }
        return result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
