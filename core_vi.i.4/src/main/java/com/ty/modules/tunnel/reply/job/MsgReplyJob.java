package com.ty.modules.tunnel.reply.job;

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
 * Created by ljb on 2017/4/18 14:40.
 */
@Component
public class MsgReplyJob extends QuartzJobBean {

    private Logger logger = Logger.getLogger(getClass());

    public void triggerReplyJob() {
        logger.info("轮询出发状态报告获取操作");
        //只有第三方的通道才需要定时触发
        List<MessageContainer> allContainer = Lists.newArrayList(ContainerRepo.getMessagerContainerMap().values());
        for(MessageContainer mc : allContainer) {
            if(mc instanceof AbstractThirdPartyMessageContainer) {
                ((AbstractThirdPartyMessageContainer) mc).triggerMsgReplyFetch();
            }
        }
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("轮询出发上行获取操作 - Internal");
    }

}
