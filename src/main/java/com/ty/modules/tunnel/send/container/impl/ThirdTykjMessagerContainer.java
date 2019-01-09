package com.ty.modules.tunnel.send.container.impl;

import com.google.common.collect.Lists;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.ty.common.config.Global;
import com.ty.common.mapper.JsonMapper;
import com.ty.common.utils.DateUtils;
import com.ty.common.utils.JedisUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.MsgReport;
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
import com.ty.modules.tunnel.send.container.entity.ThirdMessageSendTy;
import com.ty.modules.tunnel.send.container.entity.container.tykj.*;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Walter on 16/7/1.
 */
public class ThirdTykjMessagerContainer extends AbstractThirdPartyMessageContainer{

    private static Logger logger= Logger.getLogger(ThirdTykjMessagerContainer.class);
    private String url;
    private String userId;
    private String account;
    private String password;

    public ThirdTykjMessagerContainer(Tunnel tunnel,
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
            this.account = tunnel.getAccount();
            this.password = tunnel.getPassword();
        }
    }

    @Override
    public boolean sendMsg(MessageSend ms) {
        ThirdMessageSendTy messageSend = (ThirdMessageSendTy) ms;
        String mobile = messageSend.getMobile();
        String content = messageSend.getContent();
        try {
            HttpResponse<String> result = Unirest.post(this.url+"/sendMsg")
                    .field("sn", this.account)
                    .field("password", this.password)
                    .field("mobile", mobile)
                    .field("content", content)
                    .field("ext", messageSend.getSrcId())
                    .field("sendTime", "").asString();
            String jsonResult = result.getBody();
            logger.info(StringUtils.builderString(this.getTdName(), jsonResult));
            ResultCode resultCode = (ResultCode) JsonMapper.fromJsonString(jsonResult,ResultCode.class);
            if(resultCode!=null) {
                messageSend.setResultCode(resultCode);
                messageSend.setOriginStr(jsonResult);

                messageResponseEventProducer.onData(messageSend.getMsgResponse());
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            return false;
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
     * 获取第三方余额
     * @return
     */
    @Override
    public long getBalance() {
        HttpResponse<String> result = null;
        try {
            result = Unirest.post(this.url+"/balance")
                    .field("sn",this.account)
                    .field("password",this.password)
                    .asString();
            String json = result.getBody();
            ResultBalanceCode resultCode = (ResultBalanceCode) JsonMapper.fromJsonString(json, ResultBalanceCode.class);

            return resultCode.getData();
        } catch (UnirestException e) {
            return 0;
        }
    }
//主动获取方式triggerMsgReportFetch可以测试通过，为了名称不冲突，改为triggerMsgReportFetchPull，此方法目前不用
   // @Override
    public void triggerMsgReportFetchPull() {
        try {
            HttpResponse<String> result = Unirest.post("http://123.56.233.239:8080/msg-core-web/msg/getArrivedStatus")
                    .field("sn", this.account)
                    .field("password", this.password).asString();
            String jsonResult = result.getBody();
            // String jsonResult ="{\"data\":[{\"taskid\":\"919519606f4c4b74abc6f10c30fff10c\",\"mobile\":\"13994202134\",\"ext\":\"000507\",\"arrivedStatus\":\"0\",\"arrivedTime\":\"2018-10-17 10:35:00\"}],\"status\":{\"code\":\"0\",\"message\":\"获取状态报告成功\"}}";
            logger.debug("---wsy--ThirdTykjMessagerContainer-triggerMsgReportFetch--jsonResult--"+jsonResult);
            logger.info(StringUtils.builderString(this.getTdName(), jsonResult));

            ThirdTykjReportResult thirdTykjReportTesult = (ThirdTykjReportResult) JsonMapper.fromJsonString(jsonResult,ThirdTykjReportResult.class);

            logger.debug("---wsy--ThirdTykjMessagerContainer-triggerMsgReportFetch--thirdTykjReportTesult--"+thirdTykjReportTesult);
            if(thirdTykjReportTesult!=null && thirdTykjReportTesult.getData() != null){
                logger.debug("---wsy--ThirdTykjMessagerContainer-triggerMsgReportFetch--getMsgReportList--"+thirdTykjReportTesult.getMsgReportList());
                messageReportEventProducer.onData(thirdTykjReportTesult.getMsgReportList());
            }else{
                logger.info("返回状态报告为空");
            }
        } catch (Exception e) {
            logger.info("获取状态报告异常"+e.getMessage());
        }
    }
    @Override
    //获取状态报告
    public void triggerMsgReportFetch() {
        long end1 = System.currentTimeMillis();
        StringBuilder result = null;
        List<String> listString = new ArrayList<String>();
        try {
          List<ThirdTykjReportSimple> thirdTykjReportSimples  = convertToClass();//从redis获取数据
          List<MsgReport> msgReports=getMsgReportList(thirdTykjReportSimples);//封装成状态报告

            if (!thirdTykjReportSimples.isEmpty()) {
/*                logger.debug("---wsy--ThirdTykjMessagerContainer-triggerMsgReportFetch--getMsgReportList--" + msgReports);

                for (int i = 0; i < msgReports.size(); i++) {
                    logger.info("天元通道状态报告:" + msgReports.get(i).getMsgId());
                    logger.info("天元通道状态报告:" + msgReports.get(i).getMobile());
                    logger.info("天元通道状态报告:" + msgReports.get(i).getArrivedStatus());
                    logger.info("天元通道状态报告:" + msgReports.get(i).getArrivedTime());
                }*/
                long end2 = System.currentTimeMillis();
                logger.info("通道获取到数据进入disruptor的时间"+(end2-end1));
                messageReportEventProducer.onData(msgReports);
            } else {
                logger.info("返回状态报告为空");
            }
        } catch (Exception e) {
            logger.info("获取状态报告异常" + e.getMessage());
        }
    }

    @Override
    //获取上行报告
    public void triggerMsgReplyFetch() {
        long end1 = System.currentTimeMillis();
        StringBuilder result = null;
        List<String> listString = new ArrayList<String>();
        try {
            List<ThirdTykjReplySimple> thirdTykjReplySimples  = convertToReplyClass();//从redis获取数据
            List<MsgReply> msgReplys=getMsgReplyList(thirdTykjReplySimples);//封装成上行报告

            if (!thirdTykjReplySimples.isEmpty()) {
                long end2 = System.currentTimeMillis();
                logger.info("通道获取到上行数据进入disruptor的时间"+(end2-end1));
                messageReplyEventProducer.onData(msgReplys);
            } else {
                logger.info("返回上行报告为空");
            }
        } catch (Exception e) {
            logger.info("获取上行报告异常" + e.getMessage());
        }
    }

    /**
     * 从redis获取状态报告数据
     * @return
     */
    public List<ThirdTykjReportSimple> convertToClass() {

        long listLen = JedisUtils.llen(Global.FTCHREPORT_REDIS_KEY);

        if (listLen > 0 && Global.REDIS_GET_SWITCH == 1) {

            if (listLen >  100) {
                listLen = 100;
            }
            List<Object> list = JedisUtils.getObjectList(Global.FTCHREPORT_REDIS_KEY, 0, (listLen - 1));//从redis中获取对象
            JedisUtils.lTrim(Global.FTCHREPORT_REDIS_KEY, listLen, -1);//截取偏移量范围内的list
            try{
                if (!list.isEmpty()) {
                    List<ThirdTykjReportSimple> result = Lists.newArrayList();
                    for (Object obj : list) {
                        if (obj instanceof FetchReportParaRedisTy) {
                            FetchReportParaRedisTy m = (FetchReportParaRedisTy) obj;
                            ThirdTykjReportSimple thirdTykjReportSimple = new ThirdTykjReportSimple();
                            thirdTykjReportSimple.setTaskid(m.getMsgid());
                            thirdTykjReportSimple.setMobile(m.getMobile());
                            thirdTykjReportSimple.setArrivedStatus(m.getStat());
                            thirdTykjReportSimple.setArrivedTime(m.getArrivedTime());
                            result.add(thirdTykjReportSimple);
                        }
                    }
                    list.clear();
                    return result;
                }
            }
            catch (Exception e) {
                JedisUtils.listObjectAdd(Global.FTCHREPORT_REDIS_KEY, list);
                logger.error("\r(o)(o) - - 执行失败, - - (o)(o)\r" + e.getMessage(), e);
            }
        }
        return Collections.EMPTY_LIST;
    }


    /**
     * 从redis获取上行报告数据
     * @return
     */
    public List<ThirdTykjReplySimple> convertToReplyClass() {

        long listLen = JedisUtils.llen(Global.FTCHRERPLY_REDIS_KEY);

        if (listLen > 0 && Global.REDIS_GET_SWITCH == 1) {

            if (listLen >  100) {
                listLen = 100;
            }
            List<Object> list = JedisUtils.getObjectList(Global.FTCHRERPLY_REDIS_KEY, 0, (listLen - 1));//从redis中获取对象
            JedisUtils.lTrim(Global.FTCHRERPLY_REDIS_KEY, listLen, -1);//截取偏移量范围内的list
            try{
                if (!list.isEmpty()) {
                    List<ThirdTykjReplySimple> result = Lists.newArrayList();
                    for (Object obj : list) {
                        if (obj instanceof ThirdTykjReplyRedis) {
                            ThirdTykjReplyRedis m = (ThirdTykjReplyRedis) obj;
                            ThirdTykjReplySimple thirdTykjReplySimpleSimple = new ThirdTykjReplySimple();
                            thirdTykjReplySimpleSimple.setMobile(m.getMobile());
                            thirdTykjReplySimpleSimple.setContent(m.getContent());
                            thirdTykjReplySimpleSimple.setExt(m.getExt());
                            thirdTykjReplySimpleSimple.setReplyTime(m.getReplyTime());
                            result.add(thirdTykjReplySimpleSimple);
                        }
                    }
                    list.clear();
                    return result;
                }
            }
            catch (Exception e) {
                JedisUtils.listObjectAdd(Global.FTCHRERPLY_REDIS_KEY, list);
                logger.error("\r(o)(o) - - 执行失败, - - (o)(o)\r" + e.getMessage(), e);
            }
        }
        return Collections.EMPTY_LIST;
    }
/*
* 封装为MsgReport
* */
    public List<MsgReport> getMsgReportList(List<ThirdTykjReportSimple> thirdTykjReportSimples){
        List<MsgReport> result = Lists.newArrayList();
        if(thirdTykjReportSimples!=null){
            for(ThirdTykjReportSimple thirdTykjReportSimple:thirdTykjReportSimples){
                MsgReport msgReport = new MsgReport();
                msgReport.setMsgId(thirdTykjReportSimple.getTaskid());
                msgReport.setMobile(thirdTykjReportSimple.getMobile());
/*                    msgReport.setArrivedStatus(arrivedStatusReport.getArrivedStatus()=="0"?"1":"0");
                    msgReport.setArrivedResultMessage((arrivedStatusReport.getArrivedStatus()=="1")?"DELIVRD":"NO_DELIVRD");*/
                msgReport.setArrivedStatus("1");
                msgReport.setArrivedResultMessage("DELIVRD");
                msgReport.setArrivedTime(DateUtils.parseDate(thirdTykjReportSimple.getArrivedTime()));
                result.add(msgReport);

            }
        }
        return result;
    }

    /*
     * 封装为MsgReply
     * */
    public List<MsgReply> getMsgReplyList(List<ThirdTykjReplySimple> thirdTykjReplySimples){
        List<MsgReply> result = Lists.newArrayList();
        if(thirdTykjReplySimples!=null){
            for(ThirdTykjReplySimple thirdTykjReplySimple:thirdTykjReplySimples){
                MsgReply msgReply = new MsgReply();
                msgReply.setMobile(thirdTykjReplySimple.getMobile());
                msgReply.setContent(thirdTykjReplySimple.getContent());
                msgReply.setSrcId(thirdTykjReplySimple.getExt());
                result.add(msgReply);

            }
        }
        return result;
    }
}



