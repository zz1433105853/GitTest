package com.ty.modules.msg.dao;

import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;
import com.ty.modules.msg.entity.Config;

@MyBatisDao
public interface ConfigDao extends CrudDao<Config> {

}
