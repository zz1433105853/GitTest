package com.ty.modules.tunnel.send.container.entity.container.tykj;

/**
 * Created by tykfkf02 on 2016/7/12.
 */
public class ResultBalanceCode {
    private long data;
    private Status status;

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
