package com.ty.modules.tunnel.send.container.cx.impl;

import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.CxTunnel;
import com.ty.modules.msg.entity.Tunnel;
import com.ty.modules.sys.service.MsgRecordService;
import com.ty.modules.sys.service.MsgReplyService;
import com.ty.modules.sys.service.MsgReportService;
import com.ty.modules.sys.service.MsgResponseService;
import com.ty.modules.tunnel.reply.disruptor.MessageReplyEventProducer;
import com.ty.modules.tunnel.report.disruptor.MessageReportEventProducer;
import com.ty.modules.tunnel.response.disruptor.MessageResponseEventProducer;
import com.ty.modules.tunnel.send.container.cx.SdkMessageCxContainer;

/**
 * Created by ljb on 2017/4/11 15:06.
 */
public abstract class AbstractThirdCxPartyMessageContainer implements SdkMessageCxContainer {

    /**
     * 通道对象
     */
    protected CxTunnel cxTunnel;

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


    public AbstractThirdCxPartyMessageContainer(
                                              CxTunnel cxTunnel,
                                              MsgResponseService msgResponseService,
                                              MsgReportService msgReportService
    ) {
        this.msgResponseService = msgResponseService;
        this.msgReportService = msgReportService;
        this.cxTunnel = cxTunnel;
    }

    @Override
    public String getTdName() {
        if(cxTunnel!=null) {
            return cxTunnel.getTdNameWithOutConnectNo();
        }else {
            return "";
        }
    }

    @Override
    public String getTunnelType() {
        if(cxTunnel!=null) {
            return cxTunnel.getType();
        }else {
            return "";
        }
    }

    @Override
    public CxTunnel getTunnel() {
        return cxTunnel;
    }

    @Override
    public boolean checkContainerIsActive() {
        if(cxTunnel!=null && StringUtils.isNotBlank(cxTunnel.getId())) {
            return true;
        }else {
            return false;
        }
    }

}
