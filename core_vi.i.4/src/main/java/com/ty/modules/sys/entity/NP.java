package com.ty.modules.sys.entity;

import com.ty.common.persistence.DataEntity;

/**
 * Created by tykfkf02 on 2016/7/1.
 */
public class NP extends DataEntity<NP> {
    private static final long serialVersionUID = 6499959410267525602L;
    private String mobile;
    private String isp;

    public NP() {
    }

    public NP(String mobile, String isp) {
        this.mobile = mobile;
        this.isp = isp;
    }

    public NP(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }
}
