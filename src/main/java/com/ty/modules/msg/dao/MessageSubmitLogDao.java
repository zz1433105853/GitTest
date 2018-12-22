package com.ty.modules.msg.dao;


import com.ty.modules.msg.entity.MessageSubmitLog;
import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;

import java.util.List;

/**
 * Created by Ysw on 2016/6/21.
 */
@MyBatisDao
public interface MessageSubmitLogDao extends CrudDao<MessageSubmitLog> {

    void updateStatus(MessageSubmitLog messageSubmitLog);

    List<MessageSubmitLog> findFixTimeList();

    void updateFixTimeStatus();

    int findCount(MessageSubmitLog messageSubmitLog);
}