package com.ty.modules.msg.dao;


import com.ty.modules.msg.entity.Template;
import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;

/**
 * Created by tykfkf02 on 2016/6/14.
 */
@MyBatisDao
public interface TemplateDao extends CrudDao<Template> {

}
