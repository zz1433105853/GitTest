package com.ty.modules.msg.entity;

/**
 * 状态报告
 * Created by tykfkf02 on 2016/9/26.
 */
public class ArrivedStatusReport {
    private String taskid;
    private String mobile;
    private String ext;
    private String arrivedStatus;
    private String arrivedTime;

    public ArrivedStatusReport() {
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getArrivedStatus() {
        return arrivedStatus;
    }

    public void setArrivedStatus(String arrivedStatus) {
        this.arrivedStatus = arrivedStatus;
    }

    public String getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(String arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
