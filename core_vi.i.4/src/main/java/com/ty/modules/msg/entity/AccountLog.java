package com.ty.modules.msg.entity;

import com.ty.common.persistence.DataEntity;

public class AccountLog extends DataEntity<AccountLog> {
    private Customer customer;
    private Agent agent;
    private String objectType;
    private String costType;
    private int costCount;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public int getCostCount() {
        return costCount;
    }

    public void setCostCount(int costCount) {
        this.costCount = costCount;
    }

}
