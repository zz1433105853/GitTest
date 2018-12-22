package com.ty.modules.tunnel.reply.disruptor;

import com.lmax.disruptor.EventFactory;
import com.ty.modules.tunnel.reply.entity.MessageReplyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by 阿水 on 2017/4/11 14:33.
 */
@Component
@Lazy(false)
public class MessageReplyEventFactory implements EventFactory<MessageReplyEvent> {
    @Override
    public MessageReplyEvent newInstance() {
        return new MessageReplyEvent();
    }
}
