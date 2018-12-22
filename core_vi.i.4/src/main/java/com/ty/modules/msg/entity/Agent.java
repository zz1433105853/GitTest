package com.ty.modules.msg.entity;

import com.ty.common.utils.StringUtils;
import com.ty.common.persistence.DataEntity;


/**
 * Created by Ysw on 2016/6/13.
 * 代理商
 */
public class Agent extends DataEntity<Agent> {
    private static final long serialVersionUID = 4181814990086062035L;
    private Agent parent;
    private String name;
    private String code;
    private String contactName;
    private String contactMobile;
    private String contactQq;
    private String contactWx;
    private String address;
    private String restCountType;
    private long tradeRestCount;
    private long marketRestCount;
    private long restCount;
    private String isOem;
    private String status;
    private int assignCount;//特服号分配个数

    public Agent() {
    }

    public Agent(String id) {
        super(id);
    }

    public Agent(Agent parent) {
        this.parent = parent;
    }

    public Agent getParent() {
        if(parent==null && "1".equals(id)) {
            parent = new Agent("1");
            parent.setName("平台");
        }
        return parent;
    }

    public void setParent(Agent parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getContactQq() {
        return contactQq;
    }

    public void setContactQq(String contactQq) {
        this.contactQq = contactQq;
    }

    public String getContactWx() {
        return contactWx;
    }

    public void setContactWx(String contactWx) {
        this.contactWx = contactWx;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getRestCount() {
        return restCount;
    }

    public void setRestCount(long restCount) {
        if(StringUtils.isNotBlank(restCountType)){
            if(restCountType.equals("trade")){
                this.tradeRestCount = restCount;
            }else if(restCountType.equals("market")){
                this.marketRestCount = restCount;
            }
        }
        this.restCount = restCount;
        this.restCount = restCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAssignCount() {
        return assignCount;
    }

    public void setAssignCount(int assignCount) {
        this.assignCount = assignCount;
    }

    public String getIsOem() {
        return isOem;
    }

    public void setIsOem(String isOem) {
        this.isOem = isOem;
    }

    public long getTradeRestCount() {
        return tradeRestCount;
    }

    public void setTradeRestCount(long tradeRestCount) {
        this.tradeRestCount = tradeRestCount;
    }

    public long getMarketRestCount() {
        return marketRestCount;
    }

    public void setMarketRestCount(long marketRestCount) {
        this.marketRestCount = marketRestCount;
    }

    public String getRestCountType() {
        return restCountType;
    }

    public void setRestCountType(String restCountType) {
        this.restCountType = restCountType;
    }
}

