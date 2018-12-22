package com.ty.modules.tunnel.reply.disruptor;

import com.lmax.disruptor.WorkHandler;
import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.sys.service.MsgRecordService;
import com.ty.modules.sys.service.MsgReplyService;
import com.ty.modules.tunnel.entity.MsgReply;
import com.ty.modules.tunnel.reply.entity.MessageReplyEvent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by 阿水 on 2017/4/11 14:34.
 */
@Component
@Lazy(false)
@Scope(value = "prototype")
public class MessageReplyEventHandler implements WorkHandler<MessageReplyEvent> {

    private static Logger logger= Logger.getLogger(MessageReplyEventHandler.class);

    @Autowired
    private MsgReplyService msgReplyService;
    @Autowired
    private MsgRecordService msgRecordService;

    @Override
    public void onEvent(MessageReplyEvent event) throws Exception {
        //处理发送消息结果 插入结果数据到：ty_sms_send_reply
        if(event==null || event.getMsgReply()==null){
            return;
        }else {
            MsgReply msgReply = event.getMsgReply();
            MsgRecord msgRecord = msgRecordService.getCustomerIdBySrcId(msgReply.getSrcId());
            if(msgRecord !=null){
                msgReply.setCustomer(msgRecord.getCustomer());
                msgReply.setExt(msgRecord.getExt());
                msgReply.setcSrcId(msgRecord.getcSrcId());
                msgReplyService.save(msgReply);
            }
        }
    }
}
