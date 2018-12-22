package com.ty.modules.core.common.disruptor.message;

import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
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
 * Created by Ysw on 2016/5/26.
 */
@Component
@Lazy(false)
public class MessageDisruptor implements InitializingBean,ApplicationContextAware {
    private static Logger logger=Logger.getLogger(MessageEventHandler.class);
    @Value("${disruptor.bufferSize}")
    private int BUFFER_SIZE;
    @Value("${disruptor.threadCount}")
    private int threadCount;
    private Disruptor<MessageEvent> disruptor;
    private Executor executor = Executors.newCachedThreadPool();
    @Autowired
    private MessageEventFactory messageEventFactory;

    public MessageDisruptor() {
    }

    public Disruptor<MessageEvent> getDisruptor() {
        return disruptor;
    }

    public void setDisruptor(Disruptor<MessageEvent> disruptor) {
        this.disruptor = disruptor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        disruptor = new Disruptor<MessageEvent>(messageEventFactory, BUFFER_SIZE, executor, ProducerType.SINGLE, new YieldingWaitStrategy());
        MessageEventHandler[] mh = new MessageEventHandler[threadCount];
        for(int i=0;i<threadCount;i++) {
            mh[i] = (MessageEventHandler)applicationContext.getBean("messageEventHandler");
        }
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
        disruptor.handleEventsWithWorkerPool(mh);
        disruptor.start();
    }
    private ApplicationContext applicationContext = null;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
