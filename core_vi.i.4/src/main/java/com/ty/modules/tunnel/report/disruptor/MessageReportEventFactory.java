package com.ty.modules.tunnel.report.disruptor;

import com.lmax.disruptor.EventFactory;
import com.ty.modules.tunnel.report.entity.MessageReportEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by 阿水 on 2017/4/11 14:33.
 */
@Component
@Lazy(false)
public class MessageReportEventFactory implements EventFactory<MessageReportEvent> {
    @Override
    public MessageReportEvent newInstance() {
        return new MessageReportEvent();
    }
}
