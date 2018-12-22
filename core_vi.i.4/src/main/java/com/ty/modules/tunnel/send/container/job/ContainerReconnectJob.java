package com.ty.modules.tunnel.send.container.job;

import com.google.common.collect.Lists;
import com.ty.modules.tunnel.send.container.common.ContainerRepo;
import com.ty.modules.tunnel.send.container.type.MessageContainer;
import com.ty.modules.tunnel.send.container.type.StraightMessageContainer;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by ljb on 2017/4/18 14:36.
 */
@Component
public class ContainerReconnectJob extends QuartzJobBean {

    private Logger logger = Logger.getLogger(getClass());

    public void checkAndRestartContainer() {
        logger.info("轮询Container断开重启操作");
        List<MessageContainer> containerList = Lists.newArrayList(ContainerRepo.getMessagerContainerMap().values());
        for(MessageContainer mc : containerList) {
            if(mc instanceof StraightMessageContainer && !mc.checkContainerIsActive()) {
                logger.error(mc.getTdName()+": 已断开，重新启动中......");
                ((StraightMessageContainer) mc).reConnect(false);
            }
        }
    }


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("轮询Container断开重启操作 - Internal");
    }

}
