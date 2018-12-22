package com.ty.modules.msg.service;

import com.ty.common.config.Global;
import com.ty.common.service.CrudService;
import com.ty.common.utils.JedisUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.dao.CustomerDao;
import com.ty.modules.msg.dao.MessageSubmitLogDao;
import com.ty.modules.msg.entity.Customer;
import com.ty.modules.msg.entity.MessageSubmitLog;
import com.ty.modules.msg.entity.RedisMessageSubmitLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.action.GetLongAction;

/**
 * Created by LJB on 2016/6/21.
 */
@Service
@Transactional(readOnly = true)
public class MessageSubmitLogService extends CrudService<MessageSubmitLogDao, MessageSubmitLog> {
    @Autowired
    private CustomerDao customerDao;
    @Transactional(readOnly = false)
    public void submitLog(MessageSubmitLog messageSubmitLog) {
        if(StringUtils.inString(messageSubmitLog.getCustomer().getCostType(),"2","3")){
            //======更新客户余额==================
            //TODO 更换为redis
            //String cost = JedisUtils.get(StringUtils.generateRedisKey(Global.CACHE_CUSTOMER_COST_INFO,messageSubmitLog.getCustomer().getId()));
            JedisUtils.incrBy(StringUtils.generateRedisKey(Global.CACHE_CUSTOMER_COST_INFO,
                    messageSubmitLog.getCustomer().getId()),messageSubmitLog.getToFeeCount());
           /* Customer payCustomer = new Customer();
            payCustomer.setId(messageSubmitLog.getCustomer().getId());
            payCustomer.setRestCount(-messageSubmitLog.getToFeeCount());
            customerDao.rechargeSmsCount(payCustomer);*/
        }
        //TODO 更换为redis
        MessageSubmitLog msl = messageSubmitLog;
        RedisMessageSubmitLog rmsl = new RedisMessageSubmitLog(msl.getId(),msl.getCustomer().getId(),
                msl.getMobile(),msl.getContent(),msl.getIsTimeing(),msl.getSendTime(),msl.getFileUrl(),
                msl.getStatus(),msl.getcSrcId(),msl.getExtCode(),msl.getIp(),msl.getStatusInfo(),
                msl.getToFeeCount(),msl.getAuditBy(),msl.getContentSpecial(),msl.getCreateDate(),
                msl.getUpdateDate(),msl.getRemarks());
        JedisUtils.pushObject(Global.SUBMIT_LOG_REDIS_KEY,rmsl,0);
        //dao.insert(messageSubmitLog);
    }
}
