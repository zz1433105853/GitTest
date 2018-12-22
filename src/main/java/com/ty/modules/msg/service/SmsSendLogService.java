package com.ty.modules.msg.service;

import com.ty.common.service.CrudService;
import com.ty.modules.msg.entity.MsgReport;
import com.ty.modules.msg.dao.SmsSendLogDao;
import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.msg.entity.ReplyLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by tykfkf02 on 2016/6/29.
 */
@Service
@Transactional(readOnly = true)
public class SmsSendLogService extends CrudService<SmsSendLogDao,MsgRecord> {
    @Transactional(readOnly = false)
    public void insert(MsgRecord msgRecord) {
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

    public String loadSmsSendLog(MsgRecord msgRecord) {
        return this.dao.loadSmsSendLog(msgRecord);
    }

    public List<MsgRecord> findArrivedSendList(MsgReport msgReport) {
        return this.dao.findArrivedSendList(msgReport);
    }
    @Transactional(readOnly = false)
    public void batchUpdateArrivedSendStatus(List<MsgRecord> smsSendLogList) {
        this.dao.batchUpdateArrivedSendStatus(smsSendLogList);
    }

    public List<ReplyLog> findReplyLogList(ReplyLog replyLog) {
        return this.dao.findReplyLogList(replyLog);
    }
    @Transactional(readOnly = false)
    public void batchUpdateGetStatus(List<ReplyLog> replyLogs) {
        this.dao.batchUpdateGetStatus(replyLogs);
    }
    @Transactional(readOnly = false)
    public void batchDelete(List<MsgRecord> ispSmsSendLogList) {
        this.dao.batchDelete(ispSmsSendLogList);
    }
}
