package com.ty.modules.tunnel.send.container.entity.container.ymqy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by Ysw on 2016/7/4.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdYmqySendReportSingleResult {

    private String mobile;
    private String taskid;
    private String status;
    private String extno;
    private String receivetime;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceivetime() {
        return receivetime;
    }

    public void setReceivetime(String receivetime) {
        this.receivetime = receivetime;
    }

    public String getReceiveStatus(){
       return "10".equals(this.status)?"1":"2";
    }

    public String getExtno() {
        return extno;
    }

    public void setExtno(String extno) {
        this.extno = extno;
    }
}
