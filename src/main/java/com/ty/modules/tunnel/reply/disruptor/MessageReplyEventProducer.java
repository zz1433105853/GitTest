package com.ty.modules.tunnel.reply.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.ty.modules.tunnel.entity.MsgReply;
import com.ty.modules.tunnel.reply.entity.MessageReplyEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 阿水 on 2017/4/11 14:28.
 */

@Component
@Lazy(false)
public class MessageReplyEventProducer implements InitializingBean {

    private RingBuffer<MessageReplyEvent> ringBuffer;

    @Autowired
    private MessageReplyEventDisruptor messageReplyEventDisruptor;

    public void onData(MsgReply msgReply) {
        long sequence = ringBuffer.next();
        try {
            MessageReplyEvent event = ringBuffer.get(sequence);
            //设置消息实体的内容
            event.setMsgReply(msgReply);
        }finally {
            ringBuffer.publish(sequence);
        }
    }

    public void onData(List<MsgReply> msgReplyList) {
        if(msgReplyList!=null && !msgReplyList.isEmpty()) {
            for(MsgReply msgReply : msgReplyList) {
                onData(msgReply);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.ringBuffer = messageReplyEventDisruptor.getDisruptor().getRingBuffer();
    }


}
