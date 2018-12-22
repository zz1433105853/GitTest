package com.ty.modules.msg.dao;

import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.msg.entity.MsgReport;
import com.ty.common.persistence.CrudDao;
import com.ty.common.persistence.annotation.MyBatisDao;
import com.ty.modules.msg.entity.ReplyLog;

import java.util.List;

/**
 * Created by tykfkf02 on 2016/6/14.
 */
@MyBatisDao
public interface SmsSendLogDao extends CrudDao<MsgRecord> {
    int batchInsert(StringBuilder sql);

    String loadSmsSendLog(MsgRecord msgRecord);

    List<MsgRecord> findArrivedSendList(MsgReport msgReport);

    void batchUpdateArrivedSendStatus(List<MsgRecord> smsSendLogList);

    List<ReplyLog> findReplyLogList(ReplyLog replyLog);

    void batchUpdateGetStatus(List<ReplyLog> replyLogs);

    void batchDelete(List<MsgRecord> ispSmsSendLogList);

    MsgRecord getCustomerIdBySrcId(String srcId);
}
