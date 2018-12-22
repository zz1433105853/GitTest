package com.ty.modules.tunnel.reply.disruptor;

import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.ty.modules.tunnel.reply.entity.MessageReplyEvent;
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
 * Created by ljb on 2017/4/11 14:32.
 */
@Component
@Lazy(false)
public class MessageReplyEventDisruptor implements InitializingBean,ApplicationContextAware {

    @Value("${disruptor.bufferSize}")
    private int BUFFER_SIZE;

    @Value("${disruptor.threadCount}")
    private int threadCount;

    private Disruptor<MessageReplyEvent> disruptor;
    private Executor executor = Executors.newCachedThreadPool();
    @Autowired
    private MessageReplyEventFactory messageReplyEventFactory;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public MessageReplyEventDisruptor() {
    }

    public Disruptor<MessageReplyEvent> getDisruptor() {
        return disruptor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        disruptor = new Disruptor<MessageReplyEvent>(messageReplyEventFactory, BUFFER_SIZE, executor, ProducerType.SINGLE, new YieldingWaitStrategy());
        MessageReplyEventHandler[] mc = new MessageReplyEventHandler[threadCount];
        for(int i=0;i<threadCount;i++) {
            mc[i] = applicationContext.getBean(MessageReplyEventHandler.class);
        }
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
        disruptor.handleEventsWithWorkerPool(mc);
        disruptor.start();
    }
}
