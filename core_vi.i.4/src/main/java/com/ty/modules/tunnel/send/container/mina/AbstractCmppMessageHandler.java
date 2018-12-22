package com.ty.modules.tunnel.send.container.mina;

import com.ty.common.utils.DateUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.MsgReport;
import com.ty.modules.tunnel.entity.MsgReply;
import com.ty.modules.tunnel.reply.disruptor.MessageReplyEventProducer;
import com.ty.modules.tunnel.report.disruptor.MessageReportEventProducer;
import com.ty.modules.tunnel.response.disruptor.MessageResponseEventProducer;
import com.ty.modules.tunnel.send.container.entity.cmpp.MsgCommand;
import com.ty.modules.tunnel.send.container.entity.cmpp.MsgDeliverInterface;
import com.ty.modules.tunnel.send.container.entity.cmpp.MsgHead;
import com.ty.modules.tunnel.send.container.impl.AbstractStraightMessageContainer;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.text.ParseException;

/**
 * Created by ljb on 2017/4/11 16:09.
 */
public class AbstractCmppMessageHandler extends IoHandlerAdapter {

    private static Logger logger= Logger.getLogger(AbstractCmppMessageHandler.class);

    protected AbstractStraightMessageContainer messageContainer;
    protected MessageResponseEventProducer messageResponseEventProducer;
    protected MessageReportEventProducer messageReportEventProducer;
    protected MessageReplyEventProducer messageReplyEventProducer;

    public AbstractCmppMessageHandler(AbstractStraightMessageContainer messageContainer, MessageResponseEventProducer messageResponseEventProducer, MessageReportEventProducer messageReportEventProducer, MessageReplyEventProducer messageReplyEventProducer) {
        this.messageContainer = messageContainer;
        this.messageResponseEventProducer = messageResponseEventProducer;
        this.messageReportEventProducer = messageReportEventProducer;
        this.messageReplyEventProducer = messageReplyEventProducer;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        logger.info(StringUtils.builderString(messageContainer.getTdName(), "与短信网关建立会话成功"));
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.setAttribute("TD_ID", messageContainer.getTdName());
        logger.info(StringUtils.builderString(messageContainer.getTdName(), "与短信网关建立会话打开"));
        messageContainer.connectISMG(session);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)throws Exception {
        logger.info(StringUtils.builderString(messageContainer.getTdName(), "通道捕获异常 错误信息:", cause.getMessage()));
        cause.printStackTrace();
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        try {
            logger.info(StringUtils.builderString(messageContainer.getTdName(), "与短信网关的会话关闭成功"));
            IoConnector ioConnector = messageContainer.getIoConnector();
            if(ioConnector != null && !ioConnector.isDisposed() && !ioConnector.isDisposing()){
                ioConnector.dispose();
            }
            messageContainer.setConnected(false);

        }catch (Exception e) {
            logger.error("Session Closed Error");
            e.printStackTrace();
        }
    }

    public void manaulCloseSessionAndConnector(IoSession session) {
        if(session!=null && session.isActive() && session.isConnected() && !session.isClosing()) {
            session.closeNow();
        }
        try {
            sessionClosed(session);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)throws Exception {
        logger.info(StringUtils.builderString(messageContainer.getTdName(), "与短信网关的会话空闲中"));
    }

    @Override
    public void messageSent(IoSession session, Object message)  {
        try {
            MsgHead msgHead=(MsgHead) message;
            switch((int)msgHead.getCommandId()){
                case MsgCommand.CMPP_CONNECT:
                    logger.info(StringUtils.builderString(messageContainer.getTdName(), "已发送连接请求"));
                    break;
                case MsgCommand.CMPP_TERMINATE:
                    logger.info(StringUtils.builderString(messageContainer.getTdName(), "已发送断开连接请求"));
                    break;
                case MsgCommand.CMPP_SUBMIT:
                    logger.info(StringUtils.builderString(messageContainer.getTdName(), "已发送短信提交请求"));
                    messageContainer.setSendingMsg(true);
                    break;
                case MsgCommand.CMPP_DELIVER:
                    break;
                case MsgCommand.CMPP_QUERY:
                    logger.info(StringUtils.builderString(messageContainer.getTdName(),"已发送短信查询请求"));
                    break;
                case MsgCommand.CMPP_CANCEL:
                    break;
                case MsgCommand.CMPP_ACTIVE_TEST:
                    logger.info(StringUtils.builderString(messageContainer.getTdName(), "已发送网关链路检测请求"));
                    break;
            }
        }catch (Exception e) {
            logger.error("MSG Sent ERROR");
            e.printStackTrace();
        }
    }

    protected void handleDeliver(MsgDeliverInterface msgDeliver) {
        MsgReport msgReport = null;
        MsgReply msgReply = null;
        if(msgDeliver.getRegistered_Delivery()==1) {
            //状态报告
            msgReport = new MsgReport();
            //关联的消息ID
            msgReport.setMsgId(String.valueOf(msgDeliver.getMsg_Id_report()));
            String mobile = msgDeliver.getSrc_terminal_Id();
            if(StringUtils.isNotBlank(mobile)) mobile = mobile.replaceAll("\\u0000", "");
            msgReport.setMobile(mobile);
            msgReport.setArrivedStatus("DELIVRD".equals(msgDeliver.getStat()) ? "1" : "2");
            msgReport.setArrivedResultMessage(msgDeliver.getStat());
            try {
                msgReport.setArrivedTime(msgDeliver.getDone_time()==null? null: DateUtils.parseDate(msgDeliver.getDone_time(), "yyMMddHHmm"));
            } catch (ParseException e) {
                msgReport.setArrivedTime(null);
            }
            messageReportEventProducer.onData(msgReport);
        }else {
            //上行消息
            msgReply = new MsgReply();
            String srcId = msgDeliver.getDest_Id();
            if(srcId!=null) srcId = srcId.replaceAll("\\u0000", "");
            String mobile = msgDeliver.getSrc_terminal_Id();
            if(mobile!=null) mobile = mobile.replaceAll("\\u0000", "");
            String content = msgDeliver.getMsg_Content();

            msgReply.setMobile(mobile);
            msgReply.setContent(content);
            msgReply.setSrcId(srcId);
            messageReplyEventProducer.onData(msgReply);
        }
    }

    public AbstractStraightMessageContainer getMessageContainer() {
        return messageContainer;
    }

    public void setMessageContainer(AbstractStraightMessageContainer messageContainer) {
        this.messageContainer = messageContainer;
    }

    public MessageResponseEventProducer getMessageResponseEventProducer() {
        return messageResponseEventProducer;
    }

    public void setMessageResponseEventProducer(MessageResponseEventProducer messageResponseEventProducer) {
        this.messageResponseEventProducer = messageResponseEventProducer;
    }

    public MessageReportEventProducer getMessageReportEventProducer() {
        return messageReportEventProducer;
    }

    public void setMessageReportEventProducer(MessageReportEventProducer messageReportEventProducer) {
        this.messageReportEventProducer = messageReportEventProducer;
    }

    public MessageReplyEventProducer getMessageReplyEventProducer() {
        return messageReplyEventProducer;
    }

    public void setMessageReplyEventProducer(MessageReplyEventProducer messageReplyEventProducer) {
        this.messageReplyEventProducer = messageReplyEventProducer;
    }
}
