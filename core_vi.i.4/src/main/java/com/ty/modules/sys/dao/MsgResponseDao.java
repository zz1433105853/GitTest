package com.ty.modules.sys.dao;

import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;
import com.ty.modules.msg.entity.MsgResponse;

import java.util.List;

/**
 * Created by 阿水 on 2017/4/12 10:31.
 */
@MyBatisDao
public interface MsgResponseDao extends CrudDao<MsgResponse> {

    /**
     * 批量插入
     * @param list
     * @return
     */
    int batchInsert(List<MsgResponse> list);

    int batchInsertSql(StringBuilder sql);

    /**
     * 根据第三方返回的msgId查询之前发送的短信属于哪个客户
     * @param list
     * @return
     */
    List<MsgResponse> loadMsgResponseInfoByMsgIds(List<String> list);


}
