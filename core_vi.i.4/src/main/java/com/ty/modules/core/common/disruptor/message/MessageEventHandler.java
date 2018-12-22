package com.ty.modules.core.common.disruptor.message;


import com.google.common.collect.Lists;
import com.lmax.disruptor.WorkHandler;
import com.ty.common.config.Global;
import com.ty.common.mapper.JsonMapper;
import com.ty.common.utils.CacheUtils;
import com.ty.common.utils.DateUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.core.common.disruptor.customer.CustomerEventProducer;
import com.ty.modules.core.common.disruptor.message.utils.MessagingUtils;
import com.ty.modules.core.common.disruptor.message.utils.PremessagingUtils;
import com.ty.modules.core.common.disruptor.message.utils.SendDingUtils;
import com.ty.modules.core.common.disruptor.sendLog.SendLogEventProducer;
import com.ty.modules.msg.entity.*;
import com.ty.modules.msg.service.MessageSubmitLogService;
import com.ty.modules.msg.entity.Tunnel;
import com.ty.modules.tunnel.send.container.entity.AbstractMessageSend;
import com.ty.modules.tunnel.send.container.type.MessageContainer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ysw on 2016/5/26.
 */
@Component
@Lazy(false)
@Scope(value = "prototype")
public class MessageEventHandler implements WorkHandler<MessageEvent> {
    private static Logger logger=Logger.getLogger(MessageEventHandler.class);
    @Autowired
    private PremessagingUtils premessagingUtils;
    @Autowired
    private MessageSubmitLogService messageSubmitLogService;
    @Autowired
    private MessagingUtils messagingUtils;
    @Autowired
    private SendLogEventProducer sendLogEventProducer;
    @Autowired
    private CustomerEventProducer customerEventProducer;

    /**
     * 执行event
     * @param event
     */
    @Override
    public void onEvent(MessageEvent event){
        if(ProductCode.PRE_MESSAGE.equals(event.getMessageCore().getProductName())){
            preMessage(event.getMessageCore());
        }else if(ProductCode.SEND_SPECIAL_MESSAGE.equals(event.getMessageCore().getProductName())){
            sendSpecialMessage(event.getMessageCore());
        }
    }

    private void preMessage(MessageCore core){
        try{
            Customer customer = core.getCustomer();
            UserSubmit userSubmit = core.getUserSubmit();
            MessageSubmitLog messageSubmitLog = core.getMessageSubmitLog();
            messageSubmitLog.setCustomer(customer);
            messageSubmitLog.setMobile(userSubmit.getMobile());
            messageSubmitLog.setContent(userSubmit.getContent());
            messageSubmitLog.setContentSpecial(userSubmit.getContentSpecial());
            messageSubmitLog.setToFeeCount(userSubmit.getToFeeCount());
            messageSubmitLog.setExtCode(userSubmit.getExt());
            messageSubmitLog.setcSrcId(userSubmit.getSrcId());
            if(StringUtils.isNotBlank(userSubmit.getsendTime())){
                try{
                    Date sendTime = DateUtils.parseDate(userSubmit.getsendTime(), "yyyy-MM-dd HH:mm:ss");
                    messageSubmitLog.setSendTime(sendTime);
                }catch (ParseException e){
                    messageSubmitLog.setStatus("0");//待审核
                    messageSubmitLog.setStatusInfo("定时日期格式异常！");
                }
            }
            String resultInfo = handleSubmitLog(messageSubmitLog,customer,userSubmit);
            /*if(!"3".equals(messageSubmitLog.getStatus())){//不是免审信息发提醒
                StringBuffer contentBuffer = new StringBuffer("【待审核信息触发提醒】客户序列号：");
                contentBuffer.append(customer.getSerialNumber()).append(",特服号：")
                        .append(customer.getSpecialServiceNumber()).append(",产生");
                        if("0".equals(messageSubmitLog.getStatus())){
                            contentBuffer.append("待审核");
                        }else if("4".equals(messageSubmitLog.getStatus())){
                            contentBuffer.append("拦截");
                        }else{
                            contentBuffer.append("未知");
                        }
                        contentBuffer.append("信息").append("【");
                        contentBuffer.append(resultInfo).append("】").append(",请及时安排审核！（").append("批次号：").append(messageSubmitLog.getId()).append(")");
                SendDingUtils.sendDingMsg(contentBuffer.toString());
            }*/
            //更新客户余额，并插入提交记录
            Message message = new Message();
            message.setMessageSubmitLog(messageSubmitLog);
            customerEventProducer.onData(message);
            if ("3".equals(messageSubmitLog.getStatus()) && messageSubmitLog.getSendTime() == null){
                if(StringUtils.isBlank(messageSubmitLog.getContentSpecial())) {
                    String[] mobile = userSubmit.getMobile().split(",");
                    for(String m : mobile) {
                        MessageCore messageCore = new MessageCore(ProductCode.SEND_MESSAGE,customer,userSubmit,
                                userSubmit.getContent(),messageSubmitLog,m);
                        sendMessage(messageCore);
                    }
                }else {
                    //个性短信
                    String contentSpecial = messageSubmitLog.getContentSpecial();
                    contentSpecial = contentSpecial.replace("\n","\\n").replace("\r","\\r")
                            .replace("\r\n","\\n");
                    List<SpecialMsgVo> data = JsonMapper.getInstance().fromJson(contentSpecial, JsonMapper.getInstance().createCollectionType(ArrayList.class, SpecialMsgVo.class));
                    List<MessageSubmitLog> onTimeList = Lists.newArrayList();
                    for(SpecialMsgVo smv : data) {
                        if(StringUtils.isNotBlank(smv.getSendTime())) {
                            MessageSubmitLog msl = new MessageSubmitLog();
                            msl.setCustomer(customer);
                            msl.setMobile(smv.getMobile());
                            msl.setContent(smv.getContent());
                            msl.setSendTime(DateUtils.parseDate(smv.getSendTime()));
                            msl.setToFeeCount(getPayCount(smv.getMobile(),smv.getContent()));
                            handleSubmitLog(msl, customer);
                            msl.preInsert();
                            messageSubmitLogService.submitLog(msl);
                        }else {
                            MessageCore messageCore = new MessageCore(ProductCode.SEND_MESSAGE,customer,userSubmit,
                                    smv.getContent(),messageSubmitLog,smv.getMobile());
                            sendMessage(messageCore);
                        }
                    }
                    //submit log
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void sendMessage(MessageCore core){
        try{
            firstSend(core);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendSpecialMessage(MessageCore messageCore){
        //个性短信
        MessageSubmitLog messageSubmitLog = messageCore.getMessageSubmitLog();
        Customer customer = messageSubmitLog.getCustomer();
        String contentSpecial = messageSubmitLog.getContentSpecial();
        contentSpecial = contentSpecial.replace("\n","\\n").replace("\r","\\r")
        .replace("\r\n","\\n");
        List<SpecialMsgVo> data = JsonMapper.getInstance().fromJson(contentSpecial, JsonMapper.getInstance().createCollectionType(ArrayList.class, SpecialMsgVo.class));
        List<MessageSubmitLog> onTimeList = Lists.newArrayList();
        for(SpecialMsgVo smv : data) {
            if(StringUtils.isNotBlank(smv.getSendTime())) {
                MessageSubmitLog msl = new MessageSubmitLog();
                msl.setCustomer(customer);
                msl.setMobile(smv.getMobile());
                msl.setContent(smv.getContent());
                msl.setSendTime(DateUtils.parseDate(smv.getSendTime()));
                msl.setToFeeCount(getPayCount(smv.getMobile(),smv.getContent()));
                handleSubmitLog(msl, customer);
                msl.preInsert();
                messageSubmitLogService.submitLog(msl);
            }else {
                MessageCore mc = new MessageCore(ProductCode.SEND_MESSAGE,customer,new UserSubmit(),
                        smv.getContent(),messageSubmitLog,smv.getMobile());
                sendMessage(mc);
            }
        }
    }

    private int getPayCount(String mobile, String content){
        int count=0;
        int mobileCount = 0;
        int contentCount = 0;
        String[] s = StringUtils.split(mobile,",");
        mobileCount = s.length;
        if(content.length() <= 70){
            contentCount = 1;
        }else{
            contentCount = content.length()/67;
            if(content.length()%67 != 0){
                contentCount += 1;
            }
        }
        count = mobileCount*contentCount;
        return count;
    }


    private String handleSubmitLog(MessageSubmitLog messageSubmitLog,Customer customer,UserSubmit userSubmit) {
        //替换非签名的中文括号，并把签名置前
        String content = premessagingUtils.modifyContentToSignByIsp(customer,messageSubmitLog);
        String resultInfo = "";
        if(StringUtils.isBlank(content)){
            resultInfo = "签名不规范";
            messageSubmitLog.setStatus("4");//拦截
        }else{
            userSubmit.setContent(content);
            messageSubmitLog.setContent(content);
            if("0".equals(customer.getInfoDispenseAudit())){
                resultInfo = "用户不免审";
                messageSubmitLog.setStatus("0");//待审核
            }else if(premessagingUtils.validationSignSDK(customer,messageSubmitLog)){
                resultInfo = "签名不规范";
                messageSubmitLog.setStatus("4");//拦截
            }else if(premessagingUtils.validationKeywords(customer,messageSubmitLog.getContent())){
                resultInfo = "含有非法关键字";
                messageSubmitLog.setStatus("4");//拦截
            }else if("1".equals(customer.getInfoDispenseAudit()) &&
                    messageSubmitLog.getMobileCount() > customer.getDispenseCount()){
                resultInfo = "发送条数超过免审条数";
                messageSubmitLog.setStatus("0");//待审核
            }else if ("1".equals(customer.getInfoDispenseAudit())
                    && messageSubmitLog.getMobileCount() <= customer.getDispenseCount()){
                messageSubmitLog.setStatus("3");//免审
            }else{
                resultInfo = "状态信息异常";
                messageSubmitLog.setStatus("-1");//未知状态
            }
        }
        messageSubmitLog.setStatusInfo(resultInfo);
        return resultInfo;
    }

    private String handleSubmitLog(MessageSubmitLog messageSubmitLog,Customer customer) {
        //替换非签名的中文括号，并把签名置前
        String content = premessagingUtils.modifyContentToSignByIsp(customer,messageSubmitLog);
        String resultInfo = "";
        if(StringUtils.isBlank(content)){
            resultInfo = "签名不规范";
            messageSubmitLog.setStatus("4");//拦截
        }else{
            messageSubmitLog.setContent(content);
            if("0".equals(customer.getInfoDispenseAudit())){
                resultInfo = "用户不免审";
                messageSubmitLog.setStatus("0");//待审核
            }else if(premessagingUtils.validationSignSDK(customer,messageSubmitLog)){
                resultInfo = "签名不规范";
                messageSubmitLog.setStatus("4");//拦截
            }else if(premessagingUtils.validationKeywords(customer,messageSubmitLog.getContent())){
                resultInfo = "含有非法关键字";
                messageSubmitLog.setStatus("4");//拦截
            }else if("1".equals(customer.getInfoDispenseAudit()) &&
                    messageSubmitLog.getMobileCount() > customer.getDispenseCount()){
                resultInfo = "发送条数超过免审条数";
                messageSubmitLog.setStatus("0");//待审核
            }else if ("1".equals(customer.getInfoDispenseAudit())
                    && messageSubmitLog.getMobileCount() <= customer.getDispenseCount()){
                messageSubmitLog.setStatus("3");//免审
            }else{
                messageSubmitLog.setStatus("-1");//未知状态
                resultInfo = "状态信息异常！";
            }
        }
        messageSubmitLog.setStatusInfo(resultInfo);
        return resultInfo;
    }

    /**
     * 首次发送
     * @param messageCore
     */
    private void firstSend(MessageCore messageCore) {
        Customer customer = messageCore.getCustomer();
        UserSubmit userSubmit = messageCore.getUserSubmit();
        MessageSubmitLog messageSubmitLog = messageCore.getMessageSubmitLog();
        String mobile = messageCore.getMobile();
        String content = messageCore.getContent();

        boolean isSend = true;
        MsgRecord msgRecord = new MsgRecord();//发送短信记录
        msgRecord.setCustomer(customer);
        msgRecord.setMessageSubmitLog(messageSubmitLog);
        msgRecord.setMobile(mobile);
        msgRecord.setContent(content);
        //设置拓展编码和srcid
        if(userSubmit!=null) {
            msgRecord.setExt(userSubmit.getExt());
            msgRecord.setcSrcId(userSubmit.getSrcId());
        }
        //===============验证手机号格式=============
        if(mobile.length() != 11){
            msgRecord.setRefuseSendMessage("not_mobile");
            isSend = false;
        }else if("black".equals(customer.getBlackWhiteType()) && messagingUtils.validationBlackList(mobile,customer)){
            msgRecord.setRefuseSendMessage("in_black_list");
            isSend = false;
        }else if("white".equals(customer.getBlackWhiteType()) && !messagingUtils.valiadtionWhiteList(mobile,customer)){
            msgRecord.setRefuseSendMessage("not_in_white");
            isSend = false;
        }
        //===============验证该手机号每日发送短信次数是否太多======有bug,redis版本稳定要切换=====
        if(!Global.checkSendCount(customer, mobile)){
            msgRecord.setRefuseSendMessage("too_many_count");
            isSend = false;
        }

        //检测手机号号段是否存在
        if (messagingUtils.checkIspOfTheMobile(mobile)){
            msgRecord.setRefuseSendMessage("not_mobile_format");
            isSend = false;
        }
        //content = messagingUtils.modifyContentToSignByIsp(content);
        messageCore.setContent(content);
        msgRecord.setContent(content);
        msgRecord.setContentPayCount(msgRecord.getContentPayCount(content));
        msgRecord.preInsert();
        Message message = new Message();
        message.setMsgRecord(msgRecord);
        logger.debug("wsy_send_sendLogEventProducer.onData前");
        sendLogEventProducer.onData(message);
        if(isSend){
            Global.incrementSendCount(customer.getId(), mobile);//增加单日发送次数变量
            Msg msg = new Msg(customer.getId(), msgRecord.getId(),msgRecord.getExt(),mobile, content);
            logger.debug("wsy_doSend前"+msg.getMobile());
           doSend(customer,MsgRecord.buildFromMsg(msg));
        }
    }

    public void doSend(Customer customer,MsgRecord msgRecord){
        //执行发送信息操作,从缓存获取Customer
        msgRecord.setCustomer(customer);
        String mobile = msgRecord.getMobile();
        //分流短信,检查短信是要发往哪家运营商的
        String targetIsp = messagingUtils.getIspOfTheMobile(mobile);

        MessageContainer targetMessagerContainer = messagingUtils.getBestContainer(msgRecord.getCustomer(), targetIsp, true, false, false);
        logger.debug("wsy_getBestContainer");
        if(targetMessagerContainer==null) {
            //获取最佳从通道 - Container
            targetMessagerContainer = messagingUtils.getBestContainer(msgRecord.getCustomer(), targetIsp, false, false, false);
            if(targetMessagerContainer!=null) {
                logger.debug("主通道获取Fail，获取从通道Success："+targetMessagerContainer.getTdName()+msgRecord.getMobile());
            }else{
                logger.debug("主通道获取Fail，获取从通道也Fail Too"+msgRecord.getMobile());
            }
        }else {
            logger.debug("获取主通道Success："+targetMessagerContainer.getTdName()+msgRecord.getMobile());
        }
        if(targetMessagerContainer !=null){
            if(Global.isDm==1 && "10690729".equals(targetMessagerContainer.getTunnel().getEnterCode())){//垃圾程序，目前只限制101直连
                String beforMobile = mobile.substring(0,7);
                if(CacheUtils.get(beforMobile) == null){//不是山西号段
                    targetMessagerContainer = messagingUtils.getBestContainer(msgRecord.getCustomer(), targetIsp, false, false, false);
                }
            }
        }
        if(targetMessagerContainer!=null) {
            //获取SRC_ID
            Tunnel tunnel = targetMessagerContainer.getTunnel();
            String srcId  = messagingUtils.getSrcId(tunnel, msgRecord);
            if(StringUtils.isBlank(srcId)) {
                logger.error("无法生成SRC_ID，不发送此短信"+msgRecord.getMobile());
            }else {
                logger.info("执行发送短信操作srcId:"+srcId+"手机号："+msgRecord.getMobile());
                AbstractMessageSend messageSend = messagingUtils.generateMessageSendByContainer(targetMessagerContainer, msgRecord);
                if(messageSend!=null) {
                    //设置发送使用的相关实体
                    messageSend.setSrcId(srcId);
                    messageSend.setTunnel(tunnel);
                    targetMessagerContainer.sendMsg(messageSend);
                }else {
                    //未知通道类型，不发送
                    logger.error("未知的通道类型");
                }
            }
        }else{
            logger.info("获取通道失败，发送手机号："+mobile);
        }
    }
}
