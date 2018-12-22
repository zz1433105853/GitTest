package com.ty.modules.core.common.disruptor.customer;

import com.lmax.disruptor.WorkHandler;
import com.ty.common.config.Global;
import com.ty.common.utils.JedisUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.core.common.disruptor.message.MessageEvent;
import com.ty.modules.msg.entity.Message;
import com.ty.modules.msg.entity.MessageSubmitLog;
import com.ty.modules.msg.entity.RedisMessageSubmitLog;
import com.ty.modules.msg.service.MessageSubmitLogService;
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
public class CustomerEventHandler implements WorkHandler<MessageEvent> {

    private static Logger logger=Logger.getLogger(CustomerEventHandler.class);
    @Autowired
    private MessageSubmitLogService messageSubmitLogService;

    @Override
    public void onEvent(MessageEvent messageEvent){
        logger.info("更新客户余额--handler");
        if(messageEvent.getMessage().getMessageSubmitLog() != null ){
            Message message = messageEvent.getMessage();
            MessageSubmitLog msl = message.getMessageSubmitLog();
            try{
                if(StringUtils.inString(message.getMessageSubmitLog().getCustomer().getCostType(),"2","3")){
                    //======更新客户余额==================
                    //TODO 更换为redis
                    JedisUtils.incrBy(StringUtils.generateRedisKey(Global.CACHE_CUSTOMER_COST_INFO,
                            msl.getCustomer().getId()),msl.getToFeeCount());
                }
                //TODO 更换为redis
                RedisMessageSubmitLog rmsl = new RedisMessageSubmitLog(msl.getId(),msl.getCustomer().getId(),
                        msl.getMobile(),msl.getContent(),msl.getIsTimeing(),msl.getSendTime(),msl.getFileUrl(),
                        msl.getStatus(),msl.getcSrcId(),msl.getExtCode(),msl.getIp(),msl.getStatusInfo(),
                        msl.getToFeeCount(),msl.getAuditBy(),msl.getContentSpecial(),msl.getCreateDate(),
                        msl.getUpdateDate(),msl.getRemarks());
                JedisUtils.pushObject(Global.SUBMIT_LOG_REDIS_KEY,rmsl,0);
                message = null;
                msl = null;
                //messageSubmitLogService.submitLog(messageEvent.getMessage().getMessageSubmitLog());
            }catch (Exception e){
                logger.info("更新客户余额失败,客户customerId:"+messageEvent.getMessage().getMessageSubmitLog().getCustomer().getId()
                        +e.getMessage());
            }
        }
    }

}
