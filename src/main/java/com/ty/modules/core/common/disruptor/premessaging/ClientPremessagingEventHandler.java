package com.ty.modules.core.common.disruptor.premessaging;

import com.google.common.collect.Lists;
import com.lmax.disruptor.WorkHandler;
import com.ty.common.config.Global;
import com.ty.common.utils.CacheUtils;
import com.ty.common.utils.DateUtils;
import com.ty.common.utils.MySQLUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.core.common.disruptor.message.MessageEvent;
import com.ty.modules.core.common.disruptor.message.utils.MessagingUtils;
import com.ty.modules.core.common.disruptor.message.utils.PremessagingUtils;
import com.ty.modules.core.common.disruptor.message.utils.SendDingUtils;
import com.ty.modules.msg.entity.*;
import com.ty.modules.msg.service.MessageCxSubmitLogService;
import com.ty.modules.msg.service.MessageSubmitLogService;
import com.ty.modules.msg.service.SmsCxSendLogService;
import com.ty.modules.msg.service.SmsSendLogService;
import com.ty.modules.tunnel.send.container.cx.MessageCxContainer;
import com.ty.modules.tunnel.send.container.entity.AbstractMessageSend;
import com.ty.modules.tunnel.send.container.type.MessageContainer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by Ysw on 2016/5/28.
 */
@Component
@Lazy(false)
@Scope(value = "prototype")
public class ClientPremessagingEventHandler implements WorkHandler<MessageEvent> {

    private static Logger logger=Logger.getLogger(ClientPremessagingEventHandler.class);

    @Autowired
    private MessageSubmitLogService messageSubmitLogService;
    @Autowired
    private MessageCxSubmitLogService messageCxSubmitLogService;
    @Autowired
    private PremessagingUtils premessagingUtils;
    @Autowired
    private SmsSendLogService smsSendLogService;
    @Autowired
    private SmsCxSendLogService smsCxSendLogService;
    @Autowired
    private MessagingUtils messagingUtils;

    @Override
    public void onEvent(MessageEvent messageEvent){
        logger.info("PreMsgHandler ---- Client======>"+Thread.currentThread().getName()+"clientMsgSubmitHandler ----");
        try{
            if(messageEvent.getMessage().getMessageSend() != null && messageEvent.getMessage().getMessageSend().getReSendLogList()!=null){
                MessageSend messageSend = messageEvent.getMessage().getMessageSend();
                reSend(messageSend.getReSendLogList(), messageSend.getContainerIsp1Id(), messageSend.getContainerIsp2Id(), messageSend.getContainerIsp3Id());
            }else if(messageEvent.getMessage().getMessageSend() != null
                    && messageEvent.getMessage().getMessageSend().getMessageSubmitLog() != null){
                logger.info("客户端提交短信");
                sendMessage(messageEvent.getMessage());
            }else if(messageEvent.getMessage().getMessageSend() != null
                    && messageEvent.getMessage().getMessageSend().getMessageCxSubmitLog() != null){
                logger.info("客户端提交彩信");
                sendCx(messageEvent.getMessage());
            }else if(messageEvent.getMessage().getCxAuditSend() != null){
                CxAuditSend messageAuditSend =  messageEvent.getMessage().getCxAuditSend();
                MessageSend messageSend = new MessageSend();
                messageSend.setCustomer(messageAuditSend.getCustomer());
                messageSend.setUserSubmit(messageAuditSend.getUserSubmit());//目前发送短信只需要userSbumit中的扩展码
                messageSend.setContent(messageAuditSend.getContent());
                messageSend.setMessageCxSubmitLog(messageAuditSend.getMessageCxSubmitLog());//发送记录表需要submit_id
                messageSend.setMobile(messageAuditSend.getMobile());
                firstSendCx(messageSend);
            }else if(messageEvent.getMessage().getMessageAuditSend() != null){
                MessageAuditSend messageAuditSend =  messageEvent.getMessage().getMessageAuditSend();
                MessageSend messageSend = new MessageSend();
                messageSend.setCustomer(messageAuditSend.getCustomer());
                messageSend.setUserSubmit(messageAuditSend.getUserSubmit());//目前发送短信只需要userSbumit中的扩展码
                messageSend.setContent(messageAuditSend.getContent());
                messageSend.setMessageSubmitLog(messageAuditSend.getMessageSubmitLog());//发送记录表需要submit_id
                messageSend.setMobile(messageAuditSend.getMobile());
                firstSend(messageSend);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void sendMessage(Message mtmp){
        MessageSend ms = mtmp.getMessageSend();
        Customer customer = ms.getCustomer();
        UserSubmit userSubmit = ms.getUserSubmit();
        MessageSubmitLog messageSubmitLog = ms.getMessageSubmitLog();
        messageSubmitLog.setCustomer(customer);
        messageSubmitLog.setMobile(userSubmit.getMobile());
        messageSubmitLog.setContent(userSubmit.getContent());
        messageSubmitLog.setToFeeCount(userSubmit.getToFeeCount());
        messageSubmitLog.setExtCode(userSubmit.getExt());
        if(StringUtils.isNotBlank(userSubmit.getsendTime())){
            try{
                Date sendTime = DateUtils.parseDate(userSubmit.getsendTime(), "yyyy-MM-dd HH:mm:ss");
                messageSubmitLog.setSendTime(sendTime);
            }catch (ParseException e){
                messageSubmitLog.setStatus("0");//待审核
                messageSubmitLog.setStatusInfo("定时日期格式异常！");
            }
        }
        //替换非签名的中文括号，并把签名置前
        String content = premessagingUtils.modifyContentToSignByIsp(customer,messageSubmitLog);
        String resultInfo = "";
        if(StringUtils.isBlank(content)){
            resultInfo = "签名不规范";
            messageSubmitLog.setStatus("4");//拦截
        }else {
            messageSubmitLog.setContent(content);
            userSubmit.setContent(content);
            if ("0".equals(customer.getInfoDispenseAudit())) {
                resultInfo = "用户不免审";
                messageSubmitLog.setStatus("0");//待审核
            } else if (premessagingUtils.validationSign(customer, messageSubmitLog.getContent())) {
                resultInfo = "签名不规范";
                messageSubmitLog.setStatus("4");//拦截
            } else if (premessagingUtils.validationKeywords(customer, messageSubmitLog.getContent())) {
                resultInfo = "含有非法关键字";
                messageSubmitLog.setStatus("4");//拦截
            } else if ("1".equals(customer.getInfoDispenseAudit()) &&
                    messageSubmitLog.getMobileCount() > customer.getDispenseCount()) {
                resultInfo = "发送条数超过免审条数";
                messageSubmitLog.setStatus("0");//待审核
            } else if ("1".equals(customer.getInfoDispenseAudit())
                    && messageSubmitLog.getMobileCount() <= customer.getDispenseCount()) {
                messageSubmitLog.setStatus("3");//免审
            } else {
                resultInfo = "状态信息异常";
                messageSubmitLog.setStatus("-1");//未知状态
            }
        }
        messageSubmitLog.setStatusInfo(resultInfo);
      /*  if(!"3".equals(messageSubmitLog.getStatus())){//不是免审信息发提醒
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
        messageSubmitLogService.submitLog(messageSubmitLog);
        if ("3".equals(messageSubmitLog.getStatus()) && messageSubmitLog.getSendTime() == null){
            String mobileAll = userSubmit.getMobile()==null ? "": userSubmit.getMobile();
            MessageSend messageSend = new MessageSend();
            messageSend.setCustomer(customer);
            messageSend.setUserSubmit(userSubmit);//目前发送短信只需要userSbumit中的扩展码
            messageSend.setContent(userSubmit.getContent());
            messageSend.setMessageSubmitLog(messageSubmitLog);
            messageSend.setMobile(mobileAll);
            firstSend(messageSend);
        }
    }

    /*
    发送彩信
     */
    private void sendCx(Message mtmp){
        MessageSend ms = mtmp.getMessageSend();
        Customer customer = ms.getCustomer();
        UserSubmit userSubmit = ms.getUserSubmit();
        String content = userSubmit.getContent();
        MessageCxSubmitLog messageCxSubmitLog = ms.getMessageCxSubmitLog();
        messageCxSubmitLog.setCustomer(customer);
        messageCxSubmitLog.setMobile(userSubmit.getMobile());
        messageCxSubmitLog.setContent(content);
        messageCxSubmitLog.setTitle(userSubmit.getTitle());
        messageCxSubmitLog.setToFeeCount(userSubmit.getToFeeCount());
        messageCxSubmitLog.setExtCode(userSubmit.getExt());
        if(StringUtils.isNotBlank(userSubmit.getsendTime())){
            try{
                Date sendTime = DateUtils.parseDate(userSubmit.getsendTime(), "yyyy-MM-dd HH:mm:ss");
                messageCxSubmitLog.setSendTime(sendTime);
            }catch (ParseException e){
                messageCxSubmitLog.setStatus("0");//待审核
                messageCxSubmitLog.setStatusInfo("定时日期格式异常！");
            }
        }
        String resultInfo = "";
        userSubmit.setContent(content);
        if ("0".equals(customer.getInfoDispenseAudit())) {
            resultInfo = "用户不免审";
            messageCxSubmitLog.setStatus("0");//待审核
        } else if (premessagingUtils.validationKeywords(customer, messageCxSubmitLog.getContent())) {
            resultInfo = "含有非法关键字";
            messageCxSubmitLog.setStatus("4");//拦截
        } else if ("1".equals(customer.getInfoDispenseAudit()) &&
                messageCxSubmitLog.getMobileCount() > customer.getDispenseCount()) {
            resultInfo = "发送条数超过免审条数";
            messageCxSubmitLog.setStatus("0");//待审核
        } else if ("1".equals(customer.getInfoDispenseAudit())
                && messageCxSubmitLog.getMobileCount() <= customer.getDispenseCount()) {
            messageCxSubmitLog.setStatus("3");//免审
        } else {
            resultInfo = "状态信息异常";
            messageCxSubmitLog.setStatus("-1");//未知状态
        }
        messageCxSubmitLog.setStatusInfo(resultInfo);
        if(!"3".equals(messageCxSubmitLog.getStatus())){//不是免审信息发提醒
            StringBuffer contentBuffer = new StringBuffer("【彩信提醒】客户序列号：");
            contentBuffer.append(customer.getSerialNumber()).append(",特服号：")
                    .append(customer.getSpecialServiceNumber()).append(",产生");
            if("0".equals(messageCxSubmitLog.getStatus())){
                contentBuffer.append("待审核");
            }else if("4".equals(messageCxSubmitLog.getStatus())){
                contentBuffer.append("拦截");
            }else{
                contentBuffer.append("未知");
            }
            contentBuffer.append("信息").append("【");
            contentBuffer.append(resultInfo).append("】").append(",请及时安排审核！（").append("批次号：").append(messageCxSubmitLog.getId()).append(")");
            SendDingUtils.sendDingMsg(contentBuffer.toString());
        }
        //更新客户余额，并插入提交记录
        messageCxSubmitLogService.submitLog(messageCxSubmitLog);
        if ("3".equals(messageCxSubmitLog.getStatus()) && messageCxSubmitLog.getSendTime() == null){
            String mobileAll = userSubmit.getMobile()==null ? "": userSubmit.getMobile();
            MessageSend messageSend = new MessageSend();
            messageSend.setCustomer(customer);
            messageSend.setUserSubmit(userSubmit);//目前发送短信只需要userSbumit中的扩展码
            messageSend.setContent(userSubmit.getContent());
            messageSend.setMessageCxSubmitLog(messageCxSubmitLog);
            messageSend.setMobile(mobileAll);
            firstSendCx(messageSend);
        }
    }

    /**
     * 验证手机号并设置发送日志相关信息
     * @param mobile
     * @param msgRecord
     * @param customer
     * @return
     */
    private boolean verifyMobile(String mobile, MsgRecord msgRecord, Customer customer) {
        //===============验证手机号格式=============
        if(mobile.length() != 11){
            msgRecord.setRefuseSendMessage("not_mobile");
            return false;
        }else if("black".equals(customer.getBlackWhiteType()) && messagingUtils.validationBlackList(mobile,customer)){
            msgRecord.setRefuseSendMessage("in_black_list");
            return false;
        }else if("white".equals(customer.getBlackWhiteType()) && !messagingUtils.valiadtionWhiteList(mobile,customer)){
            msgRecord.setRefuseSendMessage("not_in_white");
            return false;
        }
        if(!Global.checkSendCount(customer, mobile)){
            msgRecord.setRefuseSendMessage("too_many_count");
            return false;
        }
        //检测手机号号段是否存在
        if (messagingUtils.checkIspOfTheMobile(mobile)){
            msgRecord.setRefuseSendMessage("not_mobile_format");
            return false;
        }
        return true;
    }

    /**
     * 验证手机号并设置发送日志相关信息
     * @param mobile
     * @param customer
     * @return
     */
    private boolean verifyMobileCx(String mobile, MsgCxRecord msgCxRecord, Customer customer) {
        //===============验证手机号格式=============
        if(mobile.length() != 11){
            msgCxRecord.setRefuseSendMessage("not_mobile");
            return false;
        }else if("black".equals(customer.getBlackWhiteType()) && messagingUtils.validationBlackList(mobile,customer)){
            msgCxRecord.setRefuseSendMessage("in_black_list");
            return false;
        }else if("white".equals(customer.getBlackWhiteType()) && !messagingUtils.valiadtionWhiteList(mobile,customer)){
            msgCxRecord.setRefuseSendMessage("not_in_white");
            return false;
        }
        if(!Global.checkSendCount(customer, mobile)){
            msgCxRecord.setRefuseSendMessage("too_many_count");
            return false;
        }
        //检测手机号号段是否存在
        if (messagingUtils.checkIspOfTheMobile(mobile)){
            msgCxRecord.setRefuseSendMessage("not_mobile_format");
            return false;
        }
        return true;
    }

    /**
     * 首次发送短信
     * @param messageSend
     */
    private void firstSend(MessageSend messageSend) {
        Customer customer = messageSend.getCustomer();
        UserSubmit userSubmit = messageSend.getUserSubmit();
        MessageSubmitLog messageSubmitLog = messageSend.getMessageSubmitLog();
        String mobile = messageSend.getMobile();
        String content = messageSend.getContent();
        String mobileAll = mobile==null ? "": mobile;
        String[] mobiles = mobileAll.split(",");

        // 1. 处理发送内容签名（前置，后置）
        //content = messagingUtils.modifyContentToSignByIsp(content);

        List<MsgRecord> ispSmsSendLogList= Lists.newArrayList();//发送列表
        List<MsgRecord> failSmsSendLogList= Lists.newArrayList();//其中不发送的列表

        for(String singleM : mobiles) {
            boolean isSend = true;
            MsgRecord msgRecord = new MsgRecord();//发送短信记录
            msgRecord.setCustomer(customer);
            msgRecord.setMessageSubmitLog(messageSubmitLog);
            msgRecord.setMobile(singleM);
            msgRecord.setContent(content);
            msgRecord.setContentPayCount(msgRecord.getContentPayCount(content));
            if(userSubmit!=null) {
                msgRecord.setcSrcId(userSubmit.getSrcId());
                msgRecord.setExt(userSubmit.getExt());
            }
            handleMobileByIsp(msgRecord, content, singleM, customer, ispSmsSendLogList,failSmsSendLogList);
        }

        //批量插入发送记录数据 - 放一个Service
        try {
            List<MsgRecord> allForInsert = Lists.newArrayList();
            if(!failSmsSendLogList.isEmpty()) {
                allForInsert.addAll(failSmsSendLogList);
            }
            if(!ispSmsSendLogList.isEmpty()) {
                allForInsert.addAll(ispSmsSendLogList);
            }
            if(!allForInsert.isEmpty()){
                StringBuilder sql = generateSaveRecordSql(allForInsert,true);
                logger.info("开始时间===>"+ DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
                smsSendLogService.batchInsert(sql);
                logger.info("结束时间===>"+ DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
                //清空内存占用
                sql = null;
                logger.info("保存发送短信日志列表记录成功");
            }
        }catch (Exception e) {
            e.printStackTrace();
            //保存发送日志异常,放弃发送,并记录日志
            logger.error("保存发送短信日志列表记录失败"+e.getMessage());
            return;
        }
        if(!ispSmsSendLogList.isEmpty()){
            doSend(ispSmsSendLogList,customer,userSubmit.getExt());
        }
    }

    /**
     * 首次发送彩信
     * @param messageSend
     */
    private void firstSendCx(MessageSend messageSend) {
        Customer customer = messageSend.getCustomer();
        UserSubmit userSubmit = messageSend.getUserSubmit();
        MessageCxSubmitLog messageCxSubmitLog = messageSend.getMessageCxSubmitLog();
        String title = userSubmit.getTitle();
        String mobile = messageSend.getMobile();
        String content = messageSend.getContent();
        String mobileAll = mobile==null ? "": mobile;
        String[] mobiles = mobileAll.split(",");

        List<MsgCxRecord> ispSmsSendLogList= Lists.newArrayList();//发送列表
        List<MsgCxRecord> failSmsSendLogList= Lists.newArrayList();//其中不发送的列表

        for(String singleM : mobiles) {
            boolean isSend = true;
            MsgCxRecord msgCxRecord = new MsgCxRecord();//发送彩信短信记录
            msgCxRecord.setCustomer(customer);
            msgCxRecord.setTitle(title);
            msgCxRecord.setMessageCxSubmitLog(messageCxSubmitLog);
            msgCxRecord.setMobile(singleM);
            msgCxRecord.setContent(content);
            if(userSubmit!=null) {
                msgCxRecord.setcSrcId(userSubmit.getSrcId());
                msgCxRecord.setExt(userSubmit.getExt());
            }
            handleMobileByIspCx(msgCxRecord, content, singleM, customer, ispSmsSendLogList,failSmsSendLogList);
        }

        //批量插入发送记录数据 - 放一个Service
        try {
            List<MsgCxRecord> allForInsert = Lists.newArrayList();
            if(!failSmsSendLogList.isEmpty()) {
                allForInsert.addAll(failSmsSendLogList);
            }
            if(!ispSmsSendLogList.isEmpty()) {
                allForInsert.addAll(ispSmsSendLogList);
            }
            if(!allForInsert.isEmpty()){
                StringBuilder sql = generateSaveRecordSqlCx(allForInsert,true);
                logger.info("开始时间===>"+ DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
                smsCxSendLogService.batchInsert(sql);
                logger.info("结束时间===>"+ DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
                //清空内存占用
                sql = null;
                logger.info("保存发送彩信日志列表记录成功");
            }
        }catch (Exception e) {
            e.printStackTrace();
            //保存发送日志异常,放弃发送,并记录日志
            logger.error("保存发送彩信日志列表记录失败"+e.getMessage());
            return;
        }
        if(!ispSmsSendLogList.isEmpty()){
            doSendCx(ispSmsSendLogList,customer,userSubmit.getExt());
        }
    }

    private StringBuilder generateSaveRecordSql(List<MsgRecord> allForInsert,boolean isFirstSend) throws Exception{
        //生成批量保存发送记录的sql
        String isResend = "0";
        if(!isFirstSend){
            isResend = "1";
        }
        StringBuilder sql = new StringBuilder("insert into ty_sms_send_record(id,submit_log_id,customer_id,is_resend,resend_id,mobile,content,content_pay_count,refuse_send_message,c_src_id,ext,add_date,create_date,update_date,remarks)values");
        for(int i=0;i<allForInsert.size();i++){
            MsgRecord msgRecord = allForInsert.get(i);
            msgRecord.preInsert();
            String resendId = "";
            if(StringUtils.isNotBlank(msgRecord.getResendId())){
                resendId = msgRecord.getResendId();
            }
            sql.append("(");
            sql.append("'"+msgRecord.getId()+"'").append(",");
            sql.append("'"+msgRecord.getMessageSubmitLog().getId()+"'").append(",");
            sql.append("'"+msgRecord.getCustomer().getId()+"'").append(",");
            sql.append("'"+isResend+"'").append(",");
            sql.append("'"+resendId+"'").append(",");
            sql.append("'"+msgRecord.getMobile()+"'").append(",");
            sql.append("'"+ MySQLUtils.mysql_real_escape_string(msgRecord.getContent())+"'").append(",");
            sql.append("'"+msgRecord.getContentPayCount()+"'").append(",");
            if(StringUtils.isBlank(msgRecord.getRefuseSendMessage())){
                sql.append("null").append(",");
            }else{
                sql.append("'"+msgRecord.getRefuseSendMessage()+"'").append(",");
            }
            sql.append("'"+msgRecord.getcSrcId()+"'").append(",");
            sql.append("'"+msgRecord.getExt()+"'").append(",");
            sql.append("now()").append(",");
            sql.append("now()").append(",");
            sql.append("now()").append(",");
            sql.append("'"+msgRecord.getRemarks()+"'").append(")");
            if(i != allForInsert.size()-1){
                sql.append(",");
            }
        }
        return sql;
    }

    private StringBuilder generateSaveRecordSqlCx(List<MsgCxRecord> allForInsert,boolean isFirstSend) throws Exception{
        //生成批量保存发送记录的sql
        StringBuilder sql = new StringBuilder("insert into ty_sms_cx_send_record(id,cx_submit_log_id,customer_id,title,mobile,content,content_pay_count,refuse_send_message,ext,create_date,update_date,remarks)values");
        for(int i=0;i<allForInsert.size();i++){
            MsgCxRecord msgRecord = allForInsert.get(i);
            msgRecord.preInsert();
            sql.append("(");
            sql.append("'"+msgRecord.getId()+"'").append(",");
            sql.append("'"+msgRecord.getMessageCxSubmitLog().getId()+"'").append(",");
            sql.append("'"+msgRecord.getCustomer().getId()+"'").append(",");
            sql.append("'"+MySQLUtils.mysql_real_escape_string(msgRecord.getTitle())+"'").append(",");
            sql.append("'"+msgRecord.getMobile()+"'").append(",");
            sql.append("'"+MySQLUtils.mysql_real_escape_string(msgRecord.getContent())+"'").append(",");
            sql.append("'"+msgRecord.getContentPayCount()+"'").append(",");
            if(StringUtils.isBlank(msgRecord.getRefuseSendMessage())){
                sql.append("null").append(",");
            }else{
                sql.append("'"+msgRecord.getRefuseSendMessage()+"'").append(",");
            }
            sql.append("'"+msgRecord.getExt()+"'").append(",");
            sql.append("now()").append(",");
            sql.append("now()").append(",");
            sql.append("'"+msgRecord.getRemarks()+"'").append(")");
            if(i != allForInsert.size()-1){
                sql.append(",");
            }
        }
        return sql;
    }

    /**
     * 根据手机号处理发送记录数据，并返回这条短信是否继续发送
     * @param msgRecord
     * @param content
     * @param mobile
     * @param customer
     * @param msgRecordList
     * @return
     */
    private void handleMobileByIsp(MsgRecord msgRecord, String content, String mobile, Customer customer, List<MsgRecord> msgRecordList,List<MsgRecord> failSmsSendLogList) {
        boolean isSend = verifyMobile(mobile, msgRecord, customer);
        if(isSend) {
            msgRecordList.add(msgRecord);
            Global.incrementSendCount(customer.getId(), mobile);
        }else{
            failSmsSendLogList.add(msgRecord);
        }
    }

    /**
     * 根据手机号处理发送记录数据，并返回这条短信是否继续发送
     * @param msgCxRecord
     * @param content
     * @param mobile
     * @param customer
     * @param msgRecordList
     * @return
     */
    private void handleMobileByIspCx(MsgCxRecord msgCxRecord, String content, String mobile, Customer customer, List<MsgCxRecord> msgRecordList,List<MsgCxRecord> failSmsSendLogList) {
        boolean isSend = verifyMobileCx(mobile, msgCxRecord, customer);
        if(isSend) {
            msgRecordList.add(msgCxRecord);
            Global.incrementSendCount(customer.getId(), mobile);
        }else{
            failSmsSendLogList.add(msgCxRecord);
        }
    }


    /**
     * 提交msgRecordList给redis服务器
     * @param msgRecordList
     */
    private void doSend(List<MsgRecord> msgRecordList,Customer customer,String ext) {
        List<Msg> msgList = Lists.newArrayList();
        for(MsgRecord msgRecord:msgRecordList){
            Msg msg = new Msg(customer.getId(),msgRecord.getId(),msgRecord.getExt(),msgRecord.getMobile(), msgRecord.getContent());
            msgList.add(msg);
        }
        Msg sendMsg = new Msg();
        sendMsg.setCustomerId(customer.getId());
        sendMsg.setExt(ext);
        sendMsg.setMsgList(msgList);
        doSend(customer,MsgRecord.buildFromMsg(sendMsg));
    }

    private void doSendCx(List<MsgCxRecord> msgCxRecordList,Customer customer,String ext) {
        List<Msg> msgList = Lists.newArrayList();
        for(MsgCxRecord msgCxRecord:msgCxRecordList){
            Msg msg = new Msg(customer.getId(),msgCxRecord.getId(),msgCxRecord.getExt(),msgCxRecord.getMobile(), msgCxRecord.getContent(),msgCxRecord.getTitle());
            msgList.add(msg);
        }
        Msg sendMsg = new Msg();
        sendMsg.setCustomerId(customer.getId());
        sendMsg.setExt(ext);
        sendMsg.setMsgList(msgList);
        doSendCx(customer,MsgCxRecord.buildFromMsg(sendMsg));
    }

    private void doSend(Customer customer,MsgRecord msgRecord){
        msgRecord.setCustomer(customer);
        if(msgRecord.getMsgRecordList()==null || msgRecord.getMsgRecordList().isEmpty()) return;
        //执行发送信息操作
        try {
            clientSend(msgRecord);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSendCx(Customer customer,MsgCxRecord msgCxRecord){
        msgCxRecord.setCustomer(customer);
        if(msgCxRecord.getMsgRecordList()==null || msgCxRecord.getMsgRecordList().isEmpty()) return;
        //执行发送信息操作
        try {
            clientSendCx(msgCxRecord);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 正常发送
     * @param msgRecord
     */
    private void clientSend(MsgRecord msgRecord) {
        List<MsgRecord> msgRecordList = msgRecord.getMsgRecordList();
        //先将等待发送的短信按照isp分组
        List<MsgRecord> isp1List = Lists.newArrayList();
        List<MsgRecord> isp2List = Lists.newArrayList();
        List<MsgRecord> isp3List = Lists.newArrayList();
        for(MsgRecord mr : msgRecordList) {
            if(mr ==null || StringUtils.isBlank(mr.getMobile())) continue;
            String targetIsp = messagingUtils.getIspOfTheMobile(mr.getMobile());
            switch (targetIsp) {
                case "1":
                    isp1List.add(mr);
                    break;
                case "2":
                    isp2List.add(mr);
                    break;
                case "3":
                    isp3List.add(mr);
                    break;
                default:
                    logger.error("未知ISP："+mr.getMobile());
                    break;
            }
        }
        if(!isp1List.isEmpty()) {
            if(StringUtils.isNotBlank(msgRecord.getIsp1())) {
                sendInManaul(isp1List, "1", msgRecord.getIsp1(), msgRecord);
            }else {
                sendInRule(isp1List, "1", msgRecord);
            }
        }
        if(!isp2List.isEmpty()) {
            if(StringUtils.isNotBlank(msgRecord.getIsp2())) {
                sendInManaul(isp2List, "2", msgRecord.getIsp2(), msgRecord);
            }else {
                sendInRule(isp2List, "2", msgRecord);
            }
        }
        if(!isp3List.isEmpty()) {
            if(StringUtils.isNotBlank(msgRecord.getIsp3())) {
                sendInManaul(isp3List, "3", msgRecord.getIsp3(), msgRecord);
            }else {
                sendInRule(isp3List, "3", msgRecord);
            }
        }
    }

    /**
     * 正常发送
     * @param msgCxRecord
     */
    private void clientSendCx(MsgCxRecord msgCxRecord) {
        List<MsgCxRecord> msgCxRecordList = msgCxRecord.getMsgRecordList();
        //先将等待发送的短信按照isp分组
        List<MsgCxRecord> isp1List = Lists.newArrayList();
        List<MsgCxRecord> isp2List = Lists.newArrayList();
        List<MsgCxRecord> isp3List = Lists.newArrayList();
        for(MsgCxRecord mr : msgCxRecordList) {
            if(mr ==null || StringUtils.isBlank(mr.getMobile())) continue;
            String targetIsp = messagingUtils.getIspOfTheMobile(mr.getMobile());
            switch (targetIsp) {
                case "1":
                    isp1List.add(mr);
                    break;
                case "2":
                    isp2List.add(mr);
                    break;
                case "3":
                    isp3List.add(mr);
                    break;
                default:
                    logger.error("未知ISP："+mr.getMobile());
                    break;
            }
        }
        if(!isp1List.isEmpty()) {
            if(StringUtils.isNotBlank(msgCxRecord.getIsp1())) {
                sendInManaulCx(isp1List, "1", msgCxRecord.getIsp1(), msgCxRecord);
            }else {
                sendInRuleCx(isp1List, "1", msgCxRecord);
            }
        }
        if(!isp2List.isEmpty()) {
            if(StringUtils.isNotBlank(msgCxRecord.getIsp2())) {
                sendInManaulCx(isp2List, "2", msgCxRecord.getIsp2(), msgCxRecord);
            }else {
                sendInRuleCx(isp2List, "2", msgCxRecord);
            }
        }
        if(!isp3List.isEmpty()) {
            if(StringUtils.isNotBlank(msgCxRecord.getIsp3())) {
                sendInManaulCx(isp3List, "3", msgCxRecord.getIsp3(), msgCxRecord);
            }else {
                sendInRuleCx(isp3List, "3", msgCxRecord);
            }
        }
    }

    /**
     * 自动按照规则发送
     */
    private void sendInRule(List<MsgRecord> msgRecordList, String isp, MsgRecord msgRecord) {
        new ClientSendThread(msgRecordList, msgRecord, isp).start();
    }

    /**
     * 自动按照规则发送彩信
     */
    private void sendInRuleCx(List<MsgCxRecord> msgRecordList, String isp, MsgCxRecord msgRecord) {
        new ClientSendCxThread(msgRecordList, msgRecord, isp).start();
    }

    /**
     * 按照指定的通道发送
     */
    private void sendInManaul(List<MsgRecord> msgRecordList, String isp,String containerId, MsgRecord msgRecord) {
        new ClientSendThread(msgRecordList, msgRecord, isp,containerId).start();
    }

    /**
     * 按照指定的通道发送彩信
     */
    private void sendInManaulCx(List<MsgCxRecord> msgRecordList, String isp,String containerId, MsgCxRecord msgRecord) {
        new ClientSendCxThread(msgRecordList, msgRecord, isp,containerId).start();
    }

    /**
     * 重发短信
     * @param reSendLogList 分组后的重发列表
     * @param isp1Id
     * @param isp2Id
     * @param isp3Id
     */
    private void reSend(List<MsgRecord> reSendLogList, String isp1Id, String isp2Id, String isp3Id) {
        if(reSendLogList==null || reSendLogList.isEmpty()) return;
        Customer customer = reSendLogList.get(0).getCustomer();
        String ext = reSendLogList.get(0).getExt();
        String content = reSendLogList.get(0).getContent();
        List<String> mList = Lists.newArrayList();
        for(MsgRecord ssl : reSendLogList) {
            if(StringUtils.isNotEmpty(ssl.getMobile())) {
                mList.add(ssl.getMobile());
            }
        }

        List<MsgRecord> ispSmsSendLogList= Lists.newArrayList();

        for(MsgRecord ssl : reSendLogList) {
            if(!messagingUtils.checkIspOfTheMobile(ssl.getMobile())){
                ispSmsSendLogList.add(ssl);
            }
        }

        if(!ispSmsSendLogList.isEmpty()) {
            try{
                //smsSendLogService.batchDelete(ispSmsSendLogList);
                StringBuilder sql = generateSaveRecordSql(ispSmsSendLogList,false);
                logger.info("开始时间===>"+ DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
                smsSendLogService.batchInsert(sql);
                logger.info("重发保存发送短信日志列表记录成功");
                //清空内存占用
                sql = null;
            }catch (Exception e){
                logger.error("重发保存发送记录失败"+e.getMessage());
            }
            logger.info("结束时间===>"+ DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
            doReSend(ispSmsSendLogList,customer,isp1Id,isp2Id,isp3Id,ext);
        }
    }

    private void doReSend(List<MsgRecord> msgRecordList,Customer customer,String isp1Id,String isp2Id,String isp3Id,String ext) {
        List<Msg> msgList = Lists.newArrayList();
        for(MsgRecord msgRecord:msgRecordList){
            Msg msg = new Msg(customer.getId(),msgRecord.getId(),msgRecord.getExt(),msgRecord.getMobile(), msgRecord.getContent());
            msgList.add(msg);
        }
        Msg sendMsg = new Msg(isp1Id,isp2Id,isp3Id,msgList);
        sendMsg.setCustomerId(customer.getId());
        sendMsg.setExt(ext);
        doSend(customer,MsgRecord.buildFromMsg(sendMsg));
    }

    /**
     * 客户端发送短信线程
     */
    class ClientSendThread extends Thread {
        private MsgRecord msgRecord;
        private List<MsgRecord> msgRecordList;
        private String isp;
        private String containerId;

        public ClientSendThread(List<MsgRecord> msgRecordList, MsgRecord msgRecord, String isp) {
            this.msgRecordList = msgRecordList;
            this.msgRecord = msgRecord;
            this.isp = isp;
        }
        public ClientSendThread(List<MsgRecord> msgRecordList, MsgRecord msgRecord, String isp,String containerId) {
            this.msgRecordList = msgRecordList;
            this.msgRecord = msgRecord;
            this.isp = isp;
            this.containerId = containerId;
        }

        @Override
        public void run() {
            MessageContainer targetMessagerContainer = null;
            if (StringUtils.isNotBlank(containerId)){
                targetMessagerContainer =messagingUtils.getBestContainerInListByTunnelId(containerId,true);
            }else{
                targetMessagerContainer = messagingUtils.getBestContainer(msgRecord.getCustomer(), isp, true, true, false);
            }
            if(targetMessagerContainer==null) return;
            if(Global.isDm==1 && targetMessagerContainer!=null && "10690729".equals(targetMessagerContainer.getTunnel().getEnterCode())){//垃圾程序，目前只限制101直连

                //找出不是山西号段的手机号
                List<MsgRecord> notShanxi = Lists.newArrayList();
                List<MsgRecord> shanxi = Lists.newArrayList();
                for(MsgRecord mr : msgRecordList) {
                    if(mr==null || StringUtils.isBlank(mr.getMobile())) continue;
                    String mobile = mr.getMobile();
                    String beforMobile = mobile.substring(0,7);
                    if(CacheUtils.get(beforMobile) == null){//不是山西号段
                        notShanxi.add(mr);
                    }else {
                        shanxi.add(mr);
                    }
                }
                if(!shanxi.isEmpty()) {
                    //发送山西手机号
                    List<AbstractMessageSend> msList = messagingUtils.handleClientMsgRecord(shanxi, targetMessagerContainer, msgRecord);
                    for(AbstractMessageSend ms : msList) {
                        if (StringUtils.isNotBlank(containerId)){
                            targetMessagerContainer =messagingUtils.getBestContainerInListByTunnelId(containerId,true);
                        }else{
                            targetMessagerContainer = messagingUtils.getBestContainer(msgRecord.getCustomer(), isp, true, true, false);
                        }
                        if(targetMessagerContainer!=null) {
                            try {
                                targetMessagerContainer.sendMsg(ms);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if(!notShanxi.isEmpty()) {
                    MessageContainer targetMessagerContainerForNotShanxi = messagingUtils.getBestContainer(msgRecord.getCustomer(), isp, false, true, false);
                    //发送非山西手机号
                    List<AbstractMessageSend> msList = messagingUtils.handleClientMsgRecord(notShanxi, targetMessagerContainerForNotShanxi, msgRecord);
                    for(AbstractMessageSend ms : msList) {
                        if (StringUtils.isNotBlank(containerId)){
                            targetMessagerContainerForNotShanxi =messagingUtils.getBestContainerInListByTunnelId(containerId,true);
                        }else{
                            targetMessagerContainerForNotShanxi = messagingUtils.getBestContainer(msgRecord.getCustomer(), isp, false, true, false);
                        }
                        if(targetMessagerContainerForNotShanxi!=null) {
                            try {
                                targetMessagerContainerForNotShanxi.sendMsg(ms);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }else {

                List<AbstractMessageSend> msList = messagingUtils.handleClientMsgRecord(msgRecordList, targetMessagerContainer, msgRecord);
                for(AbstractMessageSend ms : msList) {
                    if (StringUtils.isNotBlank(containerId)){
                        targetMessagerContainer =messagingUtils.getBestContainerInListByTunnelId(containerId,true);
                    }else{
                        targetMessagerContainer = messagingUtils.getBestContainer(msgRecord.getCustomer(), isp, true, true, false);
                    }
                    if(targetMessagerContainer!=null) {
                        try {
                            targetMessagerContainer.sendMsg(ms);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }

    /**
     * 客户端发送短信线程
     */
    class ClientSendCxThread extends Thread {
        private MsgCxRecord msgRecord;
        private List<MsgCxRecord> msgRecordList;
        private String isp;
        private String containerId;

        public ClientSendCxThread(List<MsgCxRecord> msgRecordList, MsgCxRecord msgRecord, String isp) {
            this.msgRecordList = msgRecordList;
            this.msgRecord = msgRecord;
            this.isp = isp;
        }
        public ClientSendCxThread(List<MsgCxRecord> msgRecordList, MsgCxRecord msgRecord, String isp,String containerId) {
            this.msgRecordList = msgRecordList;
            this.msgRecord = msgRecord;
            this.isp = isp;
            this.containerId = containerId;
        }

        @Override
        public void run() {
            MessageCxContainer targetMessagerContainer = null;
            if (StringUtils.isNotBlank(containerId)){
               // targetMessagerContainer =messagingUtils.getBestContainerInListByTunnelId(containerId,true);
            }else{
                targetMessagerContainer = messagingUtils.getBestCxContainer(msgRecord.getCustomer(), isp);
            }
            if(targetMessagerContainer==null) return;
            List<MsgCxRecord> msList = messagingUtils.handleClientCxMsgRecord(msgRecordList, targetMessagerContainer, msgRecord);
            for(MsgCxRecord ms : msList) {
                if(targetMessagerContainer!=null)targetMessagerContainer.sendCx(ms);
            }

        }
    }
}
