package com.ty.modules.core.common.disruptor.premessaging;

import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.ty.modules.core.common.disruptor.message.MessageEvent;
import com.ty.modules.core.common.disruptor.message.MessageEventHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Ysw on 2016/5/28.
 */
@Component
@Lazy(false)
public class ClientPremessagingEventDisruptor implements InitializingBean,ApplicationContextAware {
    private static Logger logger=Logger.getLogger(MessageEventHandler.class);
    @Value("${disruptor.bufferSize}")
    private int BUFFER_SIZE;
    @Value("${disruptor.threadCount}")
    private int threadCount;
    private Disruptor<MessageEvent> disruptor;
    private Executor executor = Executors.newCachedThreadPool();
    @Autowired
    private ClientPremessageingEventFactory clientPremessageingEventFactory;

    public ClientPremessagingEventDisruptor() {
    }

    public Disruptor<MessageEvent> getDisruptor() {
        return disruptor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        disruptor = new Disruptor<MessageEvent>(clientPremessageingEventFactory, BUFFER_SIZE, executor, ProducerType.SINGLE, new YieldingWaitStrategy());
        ClientPremessagingEventHandler[] ch = new ClientPremessagingEventHandler[threadCount];
        logger.info("===========创建clientMessageHandler线程开始==============");
        for(int i=0;i<threadCount;i++) {
            ch[i] = (ClientPremessagingEventHandler)applicationContext.getBean(ClientPremessagingEventHandler.class);
        }
        logger.info("===========创建clientMessageHandler线程结束==============");
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
        disruptor.handleEventsWithWorkerPool(ch);
        disruptor.start();
    }

    private ApplicationContext applicationContext = null;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
