package com.ty.modules.tunnel.send.container.entity.container.ah;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by Ysw on 2016/7/4.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdAhSendReportErrorResult {

    private String error;
    private String remark;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
