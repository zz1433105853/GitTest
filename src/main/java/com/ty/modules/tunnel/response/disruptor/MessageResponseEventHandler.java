package com.ty.modules.tunnel.response.disruptor;

import com.lmax.disruptor.WorkHandler;
import com.ty.common.config.Global;
import com.ty.common.utils.JedisUtils;
import com.ty.modules.msg.entity.MsgResponse;
import com.ty.modules.msg.entity.RedisMsgResponse;
import com.ty.modules.tunnel.response.entity.MessageResponseEvent;
import com.ty.modules.sys.service.MsgResponseService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 阿水 on 2017/4/11 14:34.
 */
@Component
@Lazy(false)
@Scope(value = "prototype")
public class MessageResponseEventHandler implements WorkHandler<MessageResponseEvent> {

    private static Logger logger= Logger.getLogger(MessageResponseEventHandler.class);

    @Autowired
    private MsgResponseService msgResponseService;

    @Override
    public void onEvent(MessageResponseEvent event) throws Exception {
        //@插入结果数据到：ty_sms_send_response
        if(event==null || event.getMsgResponse()==null || event.getMsgResponse().getMsgRecord()==null) return;
        MsgResponse msgResponse = event.getMsgResponse();
        try {
            if(msgResponse.isPackageSend()) {
                //批量保存
                List<MsgResponse> msgResponseList = msgResponse.toMsgResponseList();
                //TODO 更换为redis
                for(MsgResponse msgResponse1:msgResponseList){
                    RedisMsgResponse rmr = new RedisMsgResponse(msgResponse1.getId(),msgResponse1.getMsgRecord().getId(),msgResponse1.getMsgId(),
                            msgResponse1.getSrcId(),msgResponse1.getTunnel().getId(),msgResponse1.getSequenceNumber(),msgResponse1.getSendStatus(),
                            msgResponse1.getSendResultMessage(),msgResponse1.getCreateDate(),msgResponse1.getUpdateDate(),msgResponse1.getRemarks());
                    logger.debug("wsy--MessageResponseEventHandler--JedisUtils.pushObject之前");
                    JedisUtils.pushObject(Global.SEND_RESPONSE_REDIS_KEY,rmr,0);
                    logger.debug("wsy--MessageResponseEventHandler--JedisUtils.pushObject之后");
                }
                //msgResponseService.batchInsert(msgResponseList);
            }
            if(!msgResponse.isPackageSend()){
                //TODO 更换为redis
                msgResponse.preInsert();
                RedisMsgResponse rmr = new RedisMsgResponse(msgResponse.getId(),msgResponse.getMsgRecord().getId(),msgResponse.getMsgId(),
                        msgResponse.getSrcId(),msgResponse.getTunnel().getId(),msgResponse.getSequenceNumber(),msgResponse.getSendStatus(),
                        msgResponse.getSendResultMessage(),msgResponse.getCreateDate(),msgResponse.getUpdateDate(),msgResponse.getRemarks());
                JedisUtils.pushObject(Global.SEND_RESPONSE_REDIS_KEY,rmr,0);
                //msgResponseService.save(msgResponse);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
