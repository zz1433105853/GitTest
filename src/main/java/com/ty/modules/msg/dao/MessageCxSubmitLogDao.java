package com.ty.modules.msg.dao;


import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;
import com.ty.modules.msg.entity.MessageCxSubmitLog;
import com.ty.modules.msg.entity.MessageSubmitLog;

import java.util.List;

/**
 * Created by Ysw on 2016/6/21.
 */
@MyBatisDao
public interface MessageCxSubmitLogDao extends CrudDao<MessageCxSubmitLog> {

    void updateStatus(MessageSubmitLog messageSubmitLog);

    List<MessageSubmitLog> findFixTimeList();

    void updateFixTimeStatus();

    int findCount(MessageSubmitLog messageSubmitLog);
}