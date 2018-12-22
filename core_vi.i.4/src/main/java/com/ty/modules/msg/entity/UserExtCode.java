package com.ty.modules.msg.entity;

import com.ty.common.persistence.DataEntity;

/**
 * Created by Ysw on 2016/6/13.
 * 用户扩展号签名
 */
public class UserExtCode extends DataEntity<UserExtCode> {
    private Customer customer;
    private String extCode;
    private String signature;
    private String companyName;
    private String properCard;
    private String infSafeCard;
    private String status;


    public UserExtCode() {
    }

    public UserExtCode(String id) {
        super(id);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getProperCard() {
        return properCard;
    }

    public void setProperCard(String properCard) {
        this.properCard = properCard;
    }

    public String getInfSafeCard() {
        return infSafeCard;
    }

    public void setInfSafeCard(String infSafeCard) {
        this.infSafeCard = infSafeCard;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}

