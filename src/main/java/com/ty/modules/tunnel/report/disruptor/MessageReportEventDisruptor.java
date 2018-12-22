package com.ty.modules.tunnel.report.disruptor;

import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.ty.modules.tunnel.report.entity.MessageReportEvent;
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
 * Created by 阿水 on 2017/4/11 14:32.
 */
@Component
@Lazy(false)
public class MessageReportEventDisruptor implements InitializingBean,ApplicationContextAware {

    @Value("${disruptor.bufferSize}")
    private int BUFFER_SIZE;

    @Value("${disruptor.threadCount}")
    private int threadCount;

    private Disruptor<MessageReportEvent> disruptor;
    private Executor executor = Executors.newCachedThreadPool();
    @Autowired
    private MessageReportEventFactory messageReportEventFactory;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public MessageReportEventDisruptor() {
    }

    public Disruptor<MessageReportEvent> getDisruptor() {
        return disruptor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        disruptor = new Disruptor<MessageReportEvent>(messageReportEventFactory, BUFFER_SIZE, executor, ProducerType.SINGLE, new YieldingWaitStrategy());
        MessageReportEventHandler[] mc = new MessageReportEventHandler[threadCount];
        for(int i=0;i<threadCount;i++) {
            mc[i] = applicationContext.getBean(MessageReportEventHandler.class);
        }
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
        disruptor.handleEventsWithWorkerPool(mc);
        disruptor.start();
    }
}
