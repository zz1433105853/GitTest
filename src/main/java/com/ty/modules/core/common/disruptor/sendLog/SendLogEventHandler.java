package com.ty.modules.core.common.disruptor.sendLog;

import com.lmax.disruptor.WorkHandler;
import com.ty.common.config.Global;
import com.ty.common.utils.JedisUtils;
import com.ty.modules.core.common.disruptor.message.MessageEvent;
import com.ty.modules.msg.entity.Message;
import com.ty.modules.msg.entity.MsgRecord;
import com.ty.modules.msg.entity.RedisMsgRecord;
import com.ty.modules.msg.service.SmsSendLogService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Ysw on 2016/5/28.
 */
@Component
@Lazy(false)
@Scope(value = "prototype")
public class SendLogEventHandler implements WorkHandler<MessageEvent> {
    private static Logger logger=Logger.getLogger(SendLogEventHandler.class);

    @Override
    public void onEvent(MessageEvent messageEvent){
        try{
           if(messageEvent.getMessage().getMsgRecord() != null){
               Message message = messageEvent.getMessage();
               MsgRecord mr = message.getMsgRecord();
               long start = System.nanoTime();
               //TODO 更换为redis
               RedisMsgRecord rmr = new RedisMsgRecord(mr.getId(),mr.getCustomer().getId(),mr.getMobile(),mr.getContent(),
                       mr.getContentPayCount(),mr.getRefuseSendMessage(),mr.getMessageSubmitLog().getId(),mr.getSequenceNumber(),
                       mr.getStartDate(),mr.getEndDate(),mr.getExt(),mr.getcSrcId(),mr.getCreateDate(),mr.getUpdateDate(),mr.getRemarks());
               JedisUtils.pushObject(Global.SEND_RECORD_REDIS_KEY,rmr,0);
               long end = System.nanoTime();
               logger.info("redis-mobile"+mr.getMobile()+"==========redis push 消耗时间："+(end-start));
               message = null;
               mr = null;
                //smsSendLogService.insert(messageEvent.getMessage().getMsgRecord());
            }
        }catch(Exception e){
            logger.error("sdk保存发送记录失败："+e.getMessage());
        }
    }
}
