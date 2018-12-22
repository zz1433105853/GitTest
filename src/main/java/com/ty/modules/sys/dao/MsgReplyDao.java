package com.ty.modules.sys.dao;

import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;
import com.ty.modules.tunnel.entity.MsgReply;

import java.util.List;

/**
 * Created by 阿水 on 2017/4/12 11:22.
 */
@MyBatisDao
public interface MsgReplyDao extends CrudDao<MsgReply> {

    /**
     * 批量插入
     * @param list
     * @return
     */
    int batchInsert(List<MsgReply> list);

}
