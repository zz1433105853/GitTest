package com.ty.modules.msg.job;

import com.ty.common.config.Global;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * 每分钟手机号发送次数清零任务
 * Created by tykfkf02 on 2016/7/7.
 */

@Component
public class ClearMobileMinuteSendCountJob extends QuartzJobBean {

    private Logger logger = Logger.getLogger(getClass());

    public void clearMobileMinuteSendCount() {
        logger.info("\r(o)(o) - - - - - - - - - - - - - (o)(o)\r");
        logger.info("\r(o)(o) - -  执行每分钟手机号发送条数清零任务- - (o)(o)\r");
        logger.info("\r(o)(o) - - - - - - - - - - - - - (o)(o)\r");
        Global.clearMinuteSendMobileCount();
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("执行每分钟手机号发送条数清零任务 - Internal");
    }
}
