package com.ty.modules.msg.dao;

import com.ty.modules.msg.entity.AccountLog;
import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface AccountLogDao extends CrudDao<AccountLog> {

}
