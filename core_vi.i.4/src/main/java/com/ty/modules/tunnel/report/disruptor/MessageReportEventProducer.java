package com.ty.modules.tunnel.report.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.ty.modules.msg.entity.MsgReport;
import com.ty.modules.tunnel.report.entity.MessageReportEvent;
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
public class MessageReportEventProducer implements InitializingBean {

    private RingBuffer<MessageReportEvent> ringBuffer;

    @Autowired
    private MessageReportEventDisruptor messageReportEventDisruptor;

    public void onData(MsgReport msgReport) {
        long sequence = ringBuffer.next();
        try {
            MessageReportEvent event = ringBuffer.get(sequence);
            //设置消息实体的内容
            event.setMsgReport(msgReport);
        }finally {
            ringBuffer.publish(sequence);
        }
    }

    public void onData(List<MsgReport> msgReportList) {
        if(msgReportList!=null && !msgReportList.isEmpty()) {
            for(MsgReport msgReport : msgReportList) {
                onData(msgReport);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.ringBuffer = messageReportEventDisruptor.getDisruptor().getRingBuffer();
    }


}
