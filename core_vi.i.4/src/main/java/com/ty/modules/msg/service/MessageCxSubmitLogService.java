package com.ty.modules.msg.service;

import com.ty.common.config.Global;
import com.ty.common.service.CrudService;
import com.ty.common.utils.JedisUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.dao.CustomerDao;
import com.ty.modules.msg.dao.MessageCxSubmitLogDao;
import com.ty.modules.msg.entity.MessageCxSubmitLog;
import com.ty.modules.msg.entity.RedisMessageSubmitLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by LJB on 2016/6/21.
 */
@Service
@Transactional(readOnly = true)
public class MessageCxSubmitLogService extends CrudService<MessageCxSubmitLogDao, MessageCxSubmitLog> {
    @Autowired
    private CustomerDao customerDao;
    @Transactional(readOnly = false)
    public void submitLog(MessageCxSubmitLog messageCxSubmitLog) {
        if(StringUtils.inString(messageCxSubmitLog.getCustomer().getCostType(),"2","3")){
            //======更新客户余额==================
            //TODO 更换为redis
            //String cost = JedisUtils.get(StringUtils.generateRedisKey(Global.CACHE_CUSTOMER_COST_INFO,messageSubmitLog.getCustomer().getId()));
            JedisUtils.incrBy(StringUtils.generateRedisKey(Global.CACHE_CUSTOMER_COST_INFO,
                    messageCxSubmitLog.getCustomer().getId()),messageCxSubmitLog.getToFeeCount());
           /* Customer payCustomer = new Customer();
            payCustomer.setId(messageSubmitLog.getCustomer().getId());
            payCustomer.setRestCount(-messageSubmitLog.getToFeeCount());
            customerDao.rechargeSmsCount(payCustomer);*/
        }

        dao.insert(messageCxSubmitLog);
    }
}
