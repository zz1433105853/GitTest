package com.ty.modules.tunnel.send.container.impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.ty.common.mapper.JaxbMapper;
import com.ty.common.utils.DateUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.MsgResponse;
import com.ty.modules.msg.entity.Tunnel;
import com.ty.modules.sys.service.MsgRecordService;
import com.ty.modules.sys.service.MsgReplyService;
import com.ty.modules.sys.service.MsgReportService;
import com.ty.modules.sys.service.MsgResponseService;
import com.ty.modules.tunnel.entity.MsgReply;
import com.ty.modules.tunnel.reply.disruptor.MessageReplyEventProducer;
import com.ty.modules.tunnel.report.disruptor.MessageReportEventProducer;
import com.ty.modules.tunnel.response.disruptor.MessageResponseEventProducer;
import com.ty.modules.tunnel.send.container.entity.MessageSend;
import com.ty.modules.tunnel.send.container.entity.ThirdMessageSendQyxs;
import com.ty.modules.tunnel.send.container.entity.container.qyxs.ThirdQyxsBalanceResult;
import com.ty.modules.tunnel.send.container.entity.container.qyxs.ThirdQyxsMoReportResult;
import com.ty.modules.tunnel.send.container.entity.container.qyxs.ThirdQyxsSendMsgResult;
import com.ty.modules.tunnel.send.container.entity.container.qyxs.ThirdQyxsSendReportResult;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Walter on 19/1/8
 */
public class ThirdQyxsMessagerContainer extends AbstractThirdPartyMessageContainer {

    private static Logger logger= Logger.getLogger(ThirdQyxsMessagerContainer.class);
    private String url;
    private String userId;
    private String account;
    private String password;

    public ThirdQyxsMessagerContainer(Tunnel tunnel,
                                      MessageResponseEventProducer messageResponseEventProducer,
                                      MessageReportEventProducer messageReportEventProducer,
                                      MessageReplyEventProducer messageReplyEventProducer,
                                      MsgRecordService msgRecordService,
                                      MsgResponseService msgResponseService,
                                      MsgReportService msgReportService,
                                      MsgReplyService msgReplyService
    ) {
        super(tunnel, messageResponseEventProducer, messageReportEventProducer, messageReplyEventProducer, msgRecordService, msgResponseService, msgReportService, msgReplyService);
        if(tunnel!=null) {
            this.url = tunnel.getUrl();
            String accountTmp = tunnel.getAccount();
            String[] uidAccount = accountTmp.split(",");
            if(uidAccount.length == 2){
                this.userId = uidAccount[0];
                this.account = uidAccount[1];
            }
            this.password = tunnel.getPassword();
        }
    }

    @Override
    //企业信使发送短信
    public boolean sendMsg(MessageSend ms) {
        logger.info("into Qyxs sendMsg");
        ThirdMessageSendQyxs messageSend = (ThirdMessageSendQyxs) ms;
        String mobile = messageSend.getMobile();
        String content = messageSend.getContent();
        try {
            HttpResponse<String> result = Unirest.post(this.url+"/sms.aspx")
                    .field("action", "send")
                    .field("userid", userId)
                    .field("account", this.account)
                    .field("password", this.password)
                    .field("mobile", mobile)
                    .field("content", content)
                    .field("sendTime", "")
                    .field("extno", messageSend.getSrcId()).asString();
            logger.info("paramter::url="+this.url);
            logger.info("paramter::userid="+this.userId);
            logger.info("paramter::account="+this.account);
            logger.info("paramter::password="+password);
            String xmlResult = result.getBody();
            logger.info("xmlResult:"+xmlResult);
            logger.info(StringUtils.builderString(this.getTdName(), xmlResult));
            ThirdQyxsSendMsgResult thirdQyxsSendMsgResult = JaxbMapper.fromXml(xmlResult, ThirdQyxsSendMsgResult.class);
            if(thirdQyxsSendMsgResult!=null) {
                messageSend.setThirdQyxsSendMsgResult(thirdQyxsSendMsgResult);
                messageSend.setOriginStr(xmlResult);
                messageResponseEventProducer.onData(messageSend.getMsgResponse());
                return true;
            }else  {
                return false;
            }
        } catch (Exception e) {
            logger.info("QYXS sengMsg shibai");
            return false;
        }
    }


    @Override
    //返回状态报告 2019/1/12 zz
    /*code is far away from bug with the animal protecting
     *  ┏┓　　　┏┓
     *┏┛┻━━━┛┻┓
     *┃　　　　　　　┃
     *┃　　　━　　　┃
     *┃　┳┛　┗┳　┃
     *┃　　　　　　　┃
     *┃　　　┻　　　┃
     *┃　　　　　　　┃
     *┗━┓　　　┏━┛
     *　　┃　　　┃神兽保佑
     *　　┃　　　┃代码无BUG！
     *　　┃　　　┗━━━┓
     *　　┃　　　　　　　┣┓
     *　　┃　　　　　　　┏┛
     *　　┗┓┓┏━┳┓┏┛
     *　　　┃┫┫　┃┫┫
     *　　　┗┻┛　┗┻┛
     *
     */
    public void triggerMsgReportFetch() {
        logger.info("into triggerMsgReportFetch方法");
        try {
            HttpResponse<String> result = Unirest.post(this.url+"/statusApi.aspx")
                    .field("action", "query")
                    .field("userid", userId)
                    .field("account",this.account)
                    .field("password", this.password).asString();
            logger.info("状态报告信息："+this.userId+this.account+this.password+this.url);
            String xmlResult = result.getBody();
            logger.info(this.getTdName()+": result "+xmlResult);
            ThirdQyxsSendReportResult thirdQyxsSendReportResult = JaxbMapper.fromXml(xmlResult, ThirdQyxsSendReportResult.class);
            if(thirdQyxsSendReportResult!=null && thirdQyxsSendReportResult.getReturnsms() != null) {
                messageReportEventProducer.onData(thirdQyxsSendReportResult.getMsgReportList());
            }else{
                logger.info("返回状态报告为空");
            }
        } catch (Exception e) {
            logger.info("获取状态报告异常"+e.getMessage());
        }

    }

    @Override
    /**
     *返回上··行报告，
     */
    public void triggerMsgReplyFetch() {
        String timestamp = DateUtils.formatDate(new Date(),"yyyyMMddHHmmss");
        try {
            HttpResponse<String> result = Unirest.post(this.url+"/v2callApi.aspx")
                    .field("action", "query")
                    .field("userid", userId)
                    .field("account", this.account)
                    .field("password",this.password).asString();
            String xmlResult = result.getBody();
            logger.info("qYXS上行"+this.getTdName()+": result "+xmlResult);
            ThirdQyxsMoReportResult thirdQyxsMoReportResult = JaxbMapper.fromXml(xmlResult, ThirdQyxsMoReportResult.class);
            if(thirdQyxsMoReportResult!=null && thirdQyxsMoReportResult.getReturnsms() != null) {

                Map<String, MsgResponse> mrMap = msgResponseService.loadMsgResponseInfoMapByMsgIds(thirdQyxsMoReportResult.getAssocatedCustomerIds());
                    logger.info("mrMap::"+mrMap);
                List<MsgReply> msgReplyList = thirdQyxsMoReportResult.getMsgReplyList(mrMap);
                    logger.info("msgReplyList:"+msgReplyList);
                messageReplyEventProducer.onData(msgReplyList);
            }else{
                logger.info("返回上行报告为空");
            }
        } catch (Exception e) {
            logger.info("获取上行报告异常"+e.getMessage());
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * 获取第三方余额 (企业信使)
     * @return
     */
    @Override
    public long getBalance() {
        HttpResponse<String> result = null;
        try {
            result = Unirest.post(this.url+"/sms.aspx")
                    .field("action", "overage")
                    .field("userid", userId)
                    .field("account", this.account)
                    .field("password",this.password)
                    .asString();
            String xmlResult = result.getBody();
            ThirdQyxsBalanceResult thirdQyxsBalanceResult = JaxbMapper.fromXml(xmlResult, ThirdQyxsBalanceResult.class);
            return thirdQyxsBalanceResult.getOverage();
        } catch (UnirestException e) {
            return 0;
        }
    }
}



