package com.ty.modules.msg.dao;

import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;
import com.ty.modules.msg.entity.MsgCxRecord;
import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.msg.entity.MsgReport;
import com.ty.modules.msg.entity.ReplyLog;

import java.util.List;

/**
 * Created by tykfkf02 on 2016/6/14.
 */
@MyBatisDao
public interface SmsCxSendLogDao extends CrudDao<MsgCxRecord> {
    int batchInsert(StringBuilder sql);

    List<MsgCxRecord> loadSmsSendLog(MsgCxRecord msgRecord);

    void batchUpdateArrivedSendStatus(List<MsgCxRecord> smsSendLogList);

    List<ReplyLog> findReplyLogList(ReplyLog replyLog);

    void batchUpdateGetStatus(List<ReplyLog> replyLogs);

    void batchDelete(List<MsgCxRecord> ispSmsSendLogList);

    MsgRecord getCustomerIdBySrcId(String srcId);
}
