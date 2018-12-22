package com.ty.modules.msg.entity;


import com.ty.common.persistence.DataEntity;

/**
 * Created by Ljb on 2016/6/14.
 */
public class Keywords extends DataEntity<Keywords> {
    private String type;//关键字类型
    private String content;//关键字内容

    public Keywords(String id) {
        super(id);
    }

    public Keywords() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
