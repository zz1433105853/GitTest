package com.ty.modules.sys.entity;

import com.ty.common.persistence.DataEntity;

/**
 * Created by tykfkf02 on 2016/7/1.
 */
public class Dm extends DataEntity<Dm> {
    private static final long serialVersionUID = 6499959410267525602L;
    private String MobileNumber;
    private String MobileArea;

    public Dm() {
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getMobileArea() {
        return MobileArea;
    }

    public void setMobileArea(String mobileArea) {
        MobileArea = mobileArea;
    }
}
