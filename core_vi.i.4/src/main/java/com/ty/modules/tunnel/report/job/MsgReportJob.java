package com.ty.modules.tunnel.report.job;

import com.google.common.collect.Lists;
import com.ty.modules.tunnel.send.container.common.ContainerRepo;
import com.ty.modules.tunnel.send.container.impl.AbstractThirdPartyMessageContainer;
import com.ty.modules.tunnel.send.container.type.MessageContainer;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 阿水 on 2017/4/18 14:29.
 */
@Component
public class MsgReportJob extends QuartzJobBean {

    private Logger logger = Logger.getLogger(getClass());

    public void triggerReportJob() {
        logger.info("轮询出发状态报告获取操作");
        //只有第三方的通道才需要定时触发
        List<MessageContainer> allContainer = Lists.newArrayList(ContainerRepo.getMessagerContainerMap().values());
        for(MessageContainer mc : allContainer) {
            if(mc instanceof AbstractThirdPartyMessageContainer) {
                ((AbstractThirdPartyMessageContainer) mc).triggerMsgReportFetch();
            }
        }
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("轮询触发状态报告获取操作 - Internal");
    }


}
