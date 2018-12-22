package com.ty.modules.msg.entity;

import com.ty.common.persistence.DataEntity;

public class Config extends DataEntity<Config> {
    private String key;
    private String value;

    public Config() {
    }

    public Config(String id) {
        super(id);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
