package com.ty.modules.tunnel.send.container.impl;

import com.google.common.collect.Maps;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.ty.common.mapper.JaxbMapper;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.MsgRecord;
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
import com.ty.modules.tunnel.send.container.entity.container.yx.ThirdYxMo;
import com.ty.modules.tunnel.send.container.entity.container.yx.ThirdYxSendMoResult;
import com.ty.modules.tunnel.send.container.entity.container.yx.ThirdYxSendMsgresult;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * 一信
 * Created by ljb on 2017/4/22 10:06.
 */
public class ThirdYxMessagerContainer extends AbstractThirdPartyMessageContainer {

    private static Logger logger= Logger.getLogger(ThirdYxMessagerContainer.class);

    private String url;
    private String account;
    private String userId;
    private String password;

    public ThirdYxMessagerContainer(Tunnel tunnel, MessageResponseEventProducer messageResponseEventProducer, MessageReportEventProducer messageReportEventProducer, MessageReplyEventProducer messageReplyEventProducer, MsgRecordService msgRecordService, MsgResponseService msgResponseService, MsgReportService msgReportService, MsgReplyService msgReplyService) {
        super(tunnel, messageResponseEventProducer, messageReportEventProducer, messageReplyEventProducer, msgRecordService, msgResponseService, msgReportService, msgReplyService);
        if(tunnel!=null) {
            this.url = tunnel.getUrl();
            this.account = tunnel.getAccount();
            String[] accountUserId = account.split(",");
            if(accountUserId.length == 2){
                this.userId = accountUserId[0];
                this.account = accountUserId[1];
            }
            this.password = tunnel.getPassword();
        }
    }

    @Override
    public long getBalance() {
        return 0L;
    }

    @Override
    public void triggerMsgReportFetch() {
        try {
            String sendUrl = url+"/mo?un="+this.account+"&pw="+this.password+"&rf=1";
            HttpResponse<String> result = Unirest.get(sendUrl).asString();
            String xmlResult = result.getBody();
            logger.info(this.getTdName()+"reportMoResult"+xmlResult);
            ThirdYxSendMoResult thirdYxSendMoResult = JaxbMapper.fromXml(xmlResult, ThirdYxSendMoResult.class);
            if(thirdYxSendMoResult!=null && thirdYxSendMoResult.getMo() != null && !thirdYxSendMoResult.getMo().isEmpty()) {//上行
                Map<String,MsgRecord> resultMsgRecord = Maps.newHashMap();
                for(ThirdYxMo thirdYxMo:thirdYxSendMoResult.getMo()){
                    if(resultMsgRecord.get(thirdYxMo.getDa()) == null){
                        MsgRecord msgRecord = msgRecordService.getCustomerIdBySrcId(thirdYxMo.getDa().replace(userId,""));
                        resultMsgRecord.put(thirdYxMo.getDa(),msgRecord);
                    }
                }
                List<MsgReply> msgReplyList = thirdYxSendMoResult.getMsgReplyList(resultMsgRecord,userId);
                messageReplyEventProducer.onData(msgReplyList);
            }
            if(thirdYxSendMoResult!=null && thirdYxSendMoResult.getDr() != null && !thirdYxSendMoResult.getDr().isEmpty()) {//状体报告
                messageReportEventProducer.onData(thirdYxSendMoResult.getMsgReportList());
            }
        } catch (Exception e) {
            logger.error(StringUtils.builderString(this.getTdName(),"moReportException:",e.getMessage()));
        }
    }

    @Override
    public void triggerMsgReplyFetch() {
    }

    @Override
    public boolean sendMsg(MessageSend ms) {
        ThirdYxSendMsgresult messageSend = (ThirdYxSendMsgresult) ms;
        String mobile = messageSend.getMobile();
        String content = null;
        try {
            content = Hex.encodeHexString(messageSend.getContent().getBytes("gb2312"));
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        try {
            String sendUrl = url+"/mt?un="+this.account+"&pw="+this.password+"&da="+mobile+"&sm="+content+"&dc=15"+"&rd=1"+"&sa="
                    +this.userId+messageSend.getSrcId();
            HttpResponse<String> result = Unirest.get(sendUrl).asString();
            String resultTunnel = result.getBody();
            logger.info(StringUtils.builderString(this.getTdName(),"result:" + resultTunnel));
            messageSend.setRes(resultTunnel);
            if(StringUtils.isNotBlank(resultTunnel)) {
                messageSend.setRemarks(resultTunnel);
                messageResponseEventProducer.onData(messageSend.getMsgResponse());
                return true;
            }else  {
                return false;
            }
        } catch (Exception e) {
            logger.error(StringUtils.builderString(this.getTdName(),"sendException:",e.getMessage()));
            return false;
        }
    }
}
