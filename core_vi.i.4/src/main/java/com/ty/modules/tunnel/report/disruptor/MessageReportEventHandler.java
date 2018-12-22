package com.ty.modules.tunnel.report.disruptor;

import com.lmax.disruptor.WorkHandler;
import com.ty.common.config.Global;
import com.ty.common.utils.JedisUtils;
import com.ty.modules.msg.entity.MsgReport;
import com.ty.modules.msg.entity.RedisMsgReport;
import com.ty.modules.tunnel.report.entity.MessageReportEvent;
import com.ty.modules.sys.service.MsgReportService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by ljb on 2017/4/11 14:34.
 */
@Component
@Lazy(false)
@Scope(value = "prototype")
public class MessageReportEventHandler implements WorkHandler<MessageReportEvent> {

    private static Logger logger= Logger.getLogger(MessageReportEventHandler.class);

    @Autowired
    private MsgReportService msgReportService;

    @Override
    public void onEvent(MessageReportEvent event) throws Exception {
        //处理发送消息结果 插入结果数据到：ty_sms_send_report
        if(event==null || event.getMsgReport()==null) return;
        //TODO 更换为redis
        MsgReport mr = event.getMsgReport();
        mr.preInsert();
        RedisMsgReport rmr = new RedisMsgReport(mr.getId(),null,mr.getMsgId(),mr.getMobile(),
                mr.getArrivedStatus(),mr.getArrivedTime(),mr.getArrivedResultMessage(),mr.getArrivedSendStatus(),mr.getCreateDate(),
                mr.getUpdateDate(),mr.getRemarks());
        JedisUtils.pushObject(Global.SEND_REPORT_REDIS_KEY,rmr,0);
        //msgReportService.save(event.getMsgReport());
    }
}
