package com.ty.modules.msg.job;

import com.ty.common.config.Global;
import com.ty.common.utils.CacheUtils;
import com.ty.modules.msg.entity.BlackWhiteList;
import com.ty.modules.msg.entity.Customer;
import com.ty.modules.msg.entity.Keywords;
import com.ty.modules.msg.service.BlackWhiteListService;
import com.ty.modules.msg.service.KeywordsService;
import com.ty.modules.sys.entity.Dict;
import com.ty.modules.sys.service.DictService;
import com.ty.modules.tunnel.send.utils.NpUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 携号转网缓存更新任务
 * Created by ljb on 2016/7/7.
 */

@Component
public class NpCacheUpdateJob extends QuartzJobBean {
    @Autowired
    private DictService dictService;
    @Autowired
    private KeywordsService keywordsService;
    @Autowired
    private BlackWhiteListService blackWhiteListService;
    private Logger logger = Logger.getLogger(getClass());
    public void npCacheUpdate() {
        logger.info("\r(o)(o) - - - - - - - - - - - - - (o)(o)\r");
        logger.info("\r(o)(o) - -  携号转网缓存更新任务- - (o)(o)\r");
        logger.info("\r(o)(o) - - - - - - - - - - - - - (o)(o)\r");
        NpUtils.initNp();
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("携号转网缓存更新任务 - Internal");
    }
}
