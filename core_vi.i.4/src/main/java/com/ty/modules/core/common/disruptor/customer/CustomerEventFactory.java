package com.ty.modules.core.common.disruptor.customer;

import com.lmax.disruptor.EventFactory;
import com.ty.modules.core.common.disruptor.message.MessageEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by Ysw on 2016/5/26.
 */
@Component
@Lazy(false)
public class CustomerEventFactory implements EventFactory<MessageEvent> {

    @Override
    public MessageEvent newInstance() {
        return new MessageEvent();
    }


}
