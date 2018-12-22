package com.ty.modules.msg.entity;


import com.ty.common.persistence.DataEntity;

/**
 * Created by Ysw on 2017/2/14.
 */
public class TemplateDetail extends DataEntity<TemplateDetail> {

    private Template template;
    private String type;
    private String content;
    private int sort;

    public TemplateDetail() {
    }

    public TemplateDetail(String id) {
        super(id);
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
