package com.ty.modules.msg.entity;


import com.google.common.collect.Lists;
import com.ty.common.persistence.DataEntity;

import java.util.List;

/**
 * Created by Ljb on 2016/6/14.
 */
public class BlackWhiteList extends DataEntity<BlackWhiteList> {
    private String type;//黑白名单类型
    private Customer customer;//客户
    private String mobile;//手机号
    private List<BlackWhiteList> blackWhiteListList = Lists.newArrayList();

    public BlackWhiteList() {
    }
    public BlackWhiteList(String id) {
        super.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public List<BlackWhiteList> getBlackWhiteListList() {
        return blackWhiteListList;
    }

    public void setBlackWhiteListList(List<BlackWhiteList> blackWhiteListList) {
        this.blackWhiteListList = blackWhiteListList;
    }
}
