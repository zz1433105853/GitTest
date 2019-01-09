package com.ty.modules.tunnel.send.container.entity.container.qyxs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Ysw on 2019/1/8 企业信使返回xml
 */
@XmlRootElement(name="returnsms")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdQyxsSendMsgResult {

    private String returnstatus;
    private String message;
    private long remainpoint;
    private String taskID;
    private long successCounts;

    public ThirdQyxsSendMsgResult() {
    }

    public ThirdQyxsSendMsgResult(String returnstatus, String message, long remainpoint, String taskID, long successCounts) {
        this.returnstatus = returnstatus;
        this.message = message;
        this.remainpoint = remainpoint;
        this.taskID = taskID;
        this.successCounts = successCounts;
    }

    public String getReturnstatus() {
        return returnstatus;
    }

    public void setReturnstatus(String returnstatus) {
        this.returnstatus = returnstatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getRemainpoint() {
        return remainpoint;
    }

    public void setRemainpoint(long remainpoint) {
        this.remainpoint = remainpoint;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public long getSuccessCounts() {
        return successCounts;
    }

    public void setSuccessCounts(long successCounts) {
        this.successCounts = successCounts;
    }

}
