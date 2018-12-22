package com.ty.modules.tunnel.response.disruptor;

import com.lmax.disruptor.EventFactory;
import com.ty.modules.tunnel.response.entity.MessageResponseEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by ljb on 2017/4/11 14:33.
 */
@Component
@Lazy(false)
public class MessageResponseEventFactory implements EventFactory<MessageResponseEvent> {
    @Override
    public MessageResponseEvent newInstance() {
        return new MessageResponseEvent();
    }
}
