package com.ty.modules.tunnel.send.container.impl;

import com.ty.common.utils.StringUtils;
import com.ty.modules.sys.service.MsgRecordService;
import com.ty.modules.sys.service.MsgReplyService;
import com.ty.modules.sys.service.MsgReportService;
import com.ty.modules.sys.service.MsgResponseService;
import com.ty.modules.msg.entity.Tunnel;
import com.ty.modules.tunnel.reply.disruptor.MessageReplyEventProducer;
import com.ty.modules.tunnel.report.disruptor.MessageReportEventProducer;
import com.ty.modules.tunnel.response.disruptor.MessageResponseEventProducer;
import com.ty.modules.tunnel.send.container.type.SdkMessageContainer;

/**
 * Created by ljb on 2017/4/11 15:06.
 */
public abstract class AbstractThirdPartyMessageContainer implements SdkMessageContainer {

    /**
     * 通道对象
     */
    protected Tunnel tunnel;

    /**
     * 发送消息结果生产者
     */
    protected MessageResponseEventProducer messageResponseEventProducer;
    /**
     * 状态报告生产者
     */
    protected MessageReportEventProducer messageReportEventProducer;
    /**
     * 上行报告生产者
     */
    protected MessageReplyEventProducer messageReplyEventProducer;


    protected MsgRecordService msgRecordService;

    protected MsgResponseService msgResponseService;

    protected MsgReportService msgReportService;

    protected MsgReplyService msgReplyService;


    public AbstractThirdPartyMessageContainer(
                                              Tunnel tunnel,
                                              MessageResponseEventProducer messageResponseEventProducer,
                                              MessageReportEventProducer messageReportEventProducer,
                                              MessageReplyEventProducer messageReplyEventProducer,
                                              MsgRecordService msgRecordService,
                                              MsgResponseService msgResponseService,
                                              MsgReportService msgReportService,
                                              MsgReplyService msgReplyService
    ) {
        this.tunnel = tunnel;
        this.messageResponseEventProducer = messageResponseEventProducer;
        this.messageReportEventProducer = messageReportEventProducer;
        this.messageReplyEventProducer = messageReplyEventProducer;
        this.msgRecordService = msgRecordService;
        this.msgResponseService = msgResponseService;
        this.msgReportService = msgReportService;
        this.msgReplyService = msgReplyService;
    }

    @Override
    public String getTdName() {
        if(tunnel!=null) {
            return tunnel.getTdNameWithOutConnectNo();
        }else {
            return "";
        }
    }

    @Override
    public String getTunnelType() {
        if(tunnel!=null) {
            return tunnel.getType();
        }else {
            return "";
        }
    }

    @Override
    public Tunnel getTunnel() {
        return tunnel;
    }

    @Override
    public boolean checkContainerIsActive() {
        if(tunnel!=null && StringUtils.isNotBlank(tunnel.getId())) {
            return true;
        }else {
            return false;
        }
    }

}
