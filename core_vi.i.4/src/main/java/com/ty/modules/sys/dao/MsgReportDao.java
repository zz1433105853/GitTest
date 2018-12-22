package com.ty.modules.sys.dao;

import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;
import com.ty.modules.msg.entity.MsgReport;

import java.util.List;

/**
 * Created by ljb on 2017/4/12 10:47.
 */
@MyBatisDao
public interface MsgReportDao extends CrudDao<MsgReport> {

    /**
     * 批量插入
     * @param list
     * @return
     */
    int batchInsert(List<MsgReport> list);

}
