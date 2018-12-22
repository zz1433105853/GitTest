package com.ty.modules.tunnel.response.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.ty.modules.msg.entity.MsgResponse;
import com.ty.modules.tunnel.response.entity.MessageResponseEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by ljb on 2017/4/11 14:28.
 */

@Component
@Lazy(false)
public class MessageResponseEventProducer implements InitializingBean {

    private RingBuffer<MessageResponseEvent> ringBuffer;

    @Autowired
    private MessageResponseEventDisruptor messageResponseEventDisruptor;

    public void onData(MsgResponse msgResponse) {
        long sequence = ringBuffer.next();
        try {
            MessageResponseEvent messageEvent = ringBuffer.get(sequence);
            //设置消息实体的内容
            messageEvent.setMsgResponse(msgResponse);
        }finally {
            ringBuffer.publish(sequence);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.ringBuffer = messageResponseEventDisruptor.getDisruptor().getRingBuffer();
    }


}
