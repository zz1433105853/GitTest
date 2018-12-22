package com.ty.modules.msg.service;

import com.ty.common.service.CrudService;
import com.ty.modules.msg.dao.SmsCxSendLogDao;
import com.ty.modules.msg.entity.MsgCxRecord;
import com.ty.modules.msg.entity.ReplyLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by tykfkf02 on 2016/6/29.
 */
@Service
@Transactional(readOnly = true)
public class SmsCxSendLogService extends CrudService<SmsCxSendLogDao,MsgCxRecord> {
    @Transactional(readOnly = false)
    public void insert(MsgCxRecord msgRecord) {
        dao.insert(msgRecord);
    }

    /**
     * 批量插入发送记录
     * @param sql
     * @return
     */
    @Transactional(readOnly = false)
    public int batchInsert(StringBuilder sql) {
        return this.dao.batchInsert(sql);
    }

    public List<MsgCxRecord> loadSmsSendLog(MsgCxRecord msgRecord) {
        return this.dao.loadSmsSendLog(msgRecord);
    }

    public List<ReplyLog> findReplyLogList(ReplyLog replyLog) {
        return this.dao.findReplyLogList(replyLog);
    }
    @Transactional(readOnly = false)
    public void batchUpdateGetStatus(List<ReplyLog> replyLogs) {
        this.dao.batchUpdateGetStatus(replyLogs);
    }
    @Transactional(readOnly = false)
    public void batchDelete(List<MsgCxRecord> ispSmsSendLogList) {
        this.dao.batchDelete(ispSmsSendLogList);
    }
}
