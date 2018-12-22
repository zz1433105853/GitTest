package com.ty.modules.core.common.disruptor.sendLog;

import com.lmax.disruptor.RingBuffer;
import com.ty.modules.core.common.disruptor.message.MessageEvent;
import com.ty.modules.msg.entity.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by Ysw on 2016/5/28.
 */
@Component
@Lazy(false)
public class SendLogEventProducer implements InitializingBean {

    private RingBuffer<MessageEvent> ringBuffer;
    @Autowired
    private SendLogEventDisruptor sendLogEventDisruptor;

    public void onData(Message message) {
        long sequence = ringBuffer.next();
        try {
            MessageEvent messageEvent = ringBuffer.get(sequence);
            messageEvent.setMessage(message);
        }finally {
            ringBuffer.publish(sequence);
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.ringBuffer = sendLogEventDisruptor.getDisruptor().getRingBuffer();
    }
}
