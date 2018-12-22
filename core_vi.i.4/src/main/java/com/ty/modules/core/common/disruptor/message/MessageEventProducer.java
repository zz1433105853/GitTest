package com.ty.modules.core.common.disruptor.message;

import com.lmax.disruptor.RingBuffer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by Ysw on 2016/5/26.
 */
@Component
@Lazy(false)
public class MessageEventProducer implements InitializingBean{

    private RingBuffer<MessageEvent> ringBuffer;
    @Autowired
    private MessageDisruptor messageDisruptor;

    public MessageEventProducer() {
    }

    public void onData(MessageCore messageCore) {
        long sequence = ringBuffer.next();
        try {
            MessageEvent messageEvent = ringBuffer.get(sequence);
            messageEvent.setMessageCore(messageCore);
        }finally {
            ringBuffer.publish(sequence);
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.ringBuffer = messageDisruptor.getDisruptor().getRingBuffer();
    }
}
