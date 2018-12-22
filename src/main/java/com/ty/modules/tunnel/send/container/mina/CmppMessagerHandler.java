package com.ty.modules.tunnel.send.container.mina;

import com.ty.common.mapper.JsonMapper;
import com.ty.common.utils.StringUtils;
import com.ty.modules.tunnel.reply.disruptor.MessageReplyEventProducer;
import com.ty.modules.tunnel.report.disruptor.MessageReportEventProducer;
import com.ty.modules.tunnel.response.disruptor.MessageResponseEventProducer;
import com.ty.modules.tunnel.send.container.entity.CmppMessageSend;
import com.ty.modules.tunnel.send.container.entity.cmpp.*;
import com.ty.modules.tunnel.send.container.impl.AbstractStraightMessageContainer;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

/**
 * Created by Ysw on 2016/5/24.
 */
public class CmppMessagerHandler extends AbstractCmppMessageHandler {

    private static Logger logger= Logger.getLogger(CmppMessagerHandler.class);

    public CmppMessagerHandler(
                            AbstractStraightMessageContainer cmppMessagerContainer,
                            MessageResponseEventProducer messageResponseEventProducer,
                            MessageReportEventProducer messageReportEventProducer,
                            MessageReplyEventProducer messageReplyEventProducer
                            ) {
        super(cmppMessagerContainer, messageResponseEventProducer, messageReportEventProducer, messageReplyEventProducer);
    }


    /**
     * 获取服务端(CMPP3.0)发过来的message
     */
    @Override
    public void messageReceived(IoSession session, Object message){

        try {
            logger.info(StringUtils.builderString("有消息来了啊！！！！！！！", JsonMapper.toJsonString(message)));
            if(message!=null){
                MsgHead msgHead=(MsgHead) message;
                switch ((int)msgHead.getCommandId()) {
                    case MsgCommand.CMPP_CONNECT_RESP:
                        MsgConnectResp msgConnectResp=(MsgConnectResp) message;
                        logger.info(StringUtils.builderString(messageContainer.getTdName(), "收到网关连接响应，响应内容:", JsonMapper.toPrettyJsonStr(msgConnectResp)));
                        if(msgConnectResp.getStatus()==0) {
                            messageContainer.setConnected(true);
                            messageContainer.startActiveTestThread();
                        }else {
                            manaulCloseSessionAndConnector(session);
                        }
                        break;
                    case MsgCommand.CMPP_TERMINATE_RESP:
                        messageContainer.setConnected(false);
                        messageContainer.stopActiveTestThread();
                        manaulCloseSessionAndConnector(session);
                        break;
                    case MsgCommand.CMPP_SUBMIT_RESP:
                        MsgSubmitResp msgSubmitResp= (MsgSubmitResp) message;

                        logger.info(StringUtils.builderString(messageContainer.getTdName(), "WINDOW KEY : ", messageContainer.getWindowMap().keySet().toString(), " | RETURN KEY : ", String.valueOf(msgSubmitResp.getSequenceId())));

                        if(messageContainer.getWindowMap().containsKey(String.valueOf(msgSubmitResp.getSequenceId()))) {
                            String keyNow = String.valueOf(msgSubmitResp.getSequenceId());
                            CmppMessageSend messageSend = (CmppMessageSend) messageContainer.getWindowMap().get(keyNow);

                            logger.info(StringUtils.builderString(messageContainer.getTdName(), "提交响应，信息id:", messageSend.getRecordId()));

                            messageContainer.getWindowMap().remove(keyNow);
                            //设置当前在发短信的提交返回信息
                            messageSend.setMsgSubmitResp(msgSubmitResp);

                            //添加当前提交短信到短信收尾Disruptor
                            messageResponseEventProducer.onData(messageSend.getMsgResponse());
                        }
                        messageContainer.setSendingMsg(false);
                        break;
                    case MsgCommand.CMPP_DELIVER:
                        MsgDeliver msgDeliver=(MsgDeliver) message;
                        logger.info(StringUtils.builderString(messageContainer.getTdName(), "收到来自网关的Deviver信息：", JsonMapper.toPrettyJsonStr(msgDeliver)));
                        //发送Deliver Resp
                        if(msgDeliver==null) break;

                        int respResult = 0;
                        long msgIdToResp = msgDeliver.getMsg_Id();
                        long seqId = msgDeliver.getSequenceId();

                        if(msgIdToResp==0) {
                            respResult = 1;
                        }
                        //响应状态报告
                        messageContainer.sendMsgDeliverResp(msgIdToResp, seqId ,respResult);
                        //业务处理
                        handleDeliver(msgDeliver);
                        break;
                    case MsgCommand.CMPP_QUERY_RESP:
                        MsgQueryResp msgQueryResp=(MsgQueryResp) message;
                        logger.info(StringUtils.builderString(messageContainer.getTdName(), "收到短信查询响应，响应内容:", JsonMapper.toPrettyJsonStr(msgQueryResp)));
                        break;
                    case MsgCommand.CMPP_CANCEL_RESP:
                        break;
                    case MsgCommand.CMPP_ACTIVE_TEST_RESP:
                        MsgActiveTestResp msgActiveTestResp=(MsgActiveTestResp) message;
                        logger.info(StringUtils.builderString(messageContainer.getTdName(), "收到链路检测响应，响应内容:", JsonMapper.toPrettyJsonStr(msgActiveTestResp)));
                        if(msgActiveTestResp!=null&&(int)msgActiveTestResp.getCommandId()==MsgCommand.CMPP_ACTIVE_TEST_RESP){
                            messageContainer.setConnected(true);
                        }
                        break;
                    case MsgCommand.CMPP_ACTIVE_TEST:
                        MsgActiveTest msgActiveTest=(MsgActiveTest) message;
                        logger.info(StringUtils.builderString(messageContainer.getTdName(), "收到链路检测，发送内容:", JsonMapper.toPrettyJsonStr(msgActiveTest)));
                        if(msgActiveTest!=null&&(int)msgActiveTest.getCommandId()==MsgCommand.CMPP_ACTIVE_TEST){
                            messageContainer.setConnected(true);
                            messageContainer.sendActiveTestResp(msgActiveTest.getSequenceId());
                        }
                        break;
                    default:
                        logger.info(StringUtils.builderString("未匹配到CommandId的信息:", JsonMapper.toPrettyJsonStr(msgHead)));
                        break;
                }
            }else{
                logger.info(StringUtils.builderString(messageContainer.getTdName(), "由于返回消息为空，关闭此连接"));
                messageContainer.setConnected(false);
                manaulCloseSessionAndConnector(session);
            }
        }catch (Exception e) {
            manaulCloseSessionAndConnector(session);
            logger.error("MSG Received ERROR");
            e.printStackTrace();
        }
    }




}
