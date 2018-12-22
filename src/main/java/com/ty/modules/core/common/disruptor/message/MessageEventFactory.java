package com.ty.modules.core.common.disruptor.message;

import com.lmax.disruptor.EventFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by Ysw on 2016/5/26.
 */
@Component
@Lazy(false)
public class MessageEventFactory implements EventFactory<MessageEvent> {

    @Override
    public MessageEvent newInstance() {
        return new MessageEvent();
    }


}
