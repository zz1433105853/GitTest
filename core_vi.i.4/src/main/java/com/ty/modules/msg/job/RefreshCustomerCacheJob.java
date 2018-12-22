package com.ty.modules.msg.job;

import com.ty.common.config.Global;
import com.ty.common.utils.CacheUtils;
import com.ty.modules.msg.entity.Customer;
import com.ty.modules.msg.service.CustomerService;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 刷新客户缓存任务
 * Created by ljb on 2016/7/7.
 */

@Component
public class RefreshCustomerCacheJob extends QuartzJobBean {
    @Autowired
    private CustomerService customerService;
    private Logger logger = Logger.getLogger(getClass());
    public void refreshCustomerCache() {
        logger.info("\r(o)(o) - - - - - - - - - - - - - (o)(o)\r");
        logger.info("\r(o)(o) - -  执行刷新客户缓存任务- - (o)(o)\r");
        logger.info("\r(o)(o) - - - - - - - - - - - - - (o)(o)\r");
        List<String> customerIds = customerService.findIdList();
        for(String id:customerIds){
            Customer customer = customerService.useingList(new Customer(id));//使用中的客户列表；
            CacheUtils.put(Global.CACHE_CUSTOMER_INFO,customer.getSerialNumber(),customer);
        }
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("执行刷新客户缓存任务 - Internal");
    }
}
