package com.ty.modules.tunnel.send.container.entity.container.tykj;

/**
 * Created by tykfkf02 on 2016/7/12.
 */
public class ResultCode {
    private String taskid;
    private Status status;

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
