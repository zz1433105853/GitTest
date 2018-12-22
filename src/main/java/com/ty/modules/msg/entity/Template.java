package com.ty.modules.msg.entity;


import com.google.common.collect.Lists;
import com.ty.common.persistence.DataEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ljb on 2016/6/14.
 */
public class Template extends DataEntity<Template> {
    private Customer customer;
    private String name;
    private String status;

    private List<TemplateDetail> templateDetailList;
    private List<TemplateDetail> templateDetailListForSave;

    public Template() {
    }

    public Template(String id) {
        super(id);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TemplateDetail> getTemplateDetailList() {
        return templateDetailList;
    }

    public void setTemplateDetailList(List<TemplateDetail> templateDetailList) {
        this.templateDetailList = templateDetailList;
    }

    public List<TemplateDetail> getTemplateDetailListForSave() {
        return templateDetailListForSave;
    }

    public void setTemplateDetailListForSave(List<TemplateDetail> templateDetailListForSave) {
        this.templateDetailListForSave = templateDetailListForSave;
    }

    public List<String> getTexts() {
        List<String> result = Lists.newLinkedList();
        if(templateDetailList!=null&& !templateDetailList.isEmpty()) {
            for(TemplateDetail td : templateDetailList) {
                if(td==null) continue;
                if("text".equals(td.getType())) {
                    result.add(td.getContent());
                }
            }
        }
        return result;
    }

    public List<String> getParams() {
        List<String> result = Lists.newLinkedList();
        if(templateDetailList!=null&& !templateDetailList.isEmpty()) {
            for(TemplateDetail td : templateDetailList) {
                if(td==null) continue;
                if("tpl".equals(td.getType())) {
                    result.add(td.getContent());
                }
            }
        }
        return result;
    }

    public String convertToFinalMsg(HttpServletRequest request) {
        String result = "";
        if(templateDetailList==null) templateDetailList = Lists.newArrayList();
        for(TemplateDetail td : templateDetailList) {
            if(td==null) continue;
            if("text".equals(td.getType())) {
                result += td.getContent();
            }else {
                result += (request.getParameter(td.getContent())==null?"":request.getParameter(td.getContent()));
            }
        }
        return result;
    }
    private Pattern pattern;
    public String convertToRegex() {
        String result = "";
        if(templateDetailList==null) templateDetailList = Lists.newArrayList();
        for(TemplateDetail td : templateDetailList) {
            if(td==null) continue;
            if("text".equals(td.getType())) {
                result += td.getContent();
            }else {
                result += "(.*)";
            }
        }
        return result;
    }

    public Pattern getPattern() {
        if(pattern==null) {
            String regex = convertToRegex();
            pattern = Pattern.compile(regex);
        }
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * 验证输入字符串是否符合模板
     * @param str
     * @return
     */
    public boolean isMatch(String str) {
        Pattern p = getPattern();
        Matcher matcher = p.matcher(str);
        return matcher.find();
    }
}
