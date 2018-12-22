package com.ty.modules.tunnel.send.container.impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.ty.common.mapper.JaxbMapper;
import com.ty.common.mapper.JsonMapper;
import com.ty.common.utils.DateUtils;
import com.ty.common.utils.Encodes;
import com.ty.common.utils.IdGen;
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
import com.ty.modules.tunnel.send.container.entity.ThirdMessageSendAh;
import com.ty.modules.tunnel.send.container.entity.ThirdMessageSendHl;
import com.ty.modules.tunnel.send.container.entity.container.ah.ThirdAhBalanceResult;
import com.ty.modules.tunnel.send.container.entity.container.ah.ThirdAhMoReportResult;
import com.ty.modules.tunnel.send.container.entity.container.ah.ThirdAhSendMsgResult;
import com.ty.modules.tunnel.send.container.entity.container.ah.ThirdAhSendReportResult;
import com.ty.modules.tunnel.send.container.entity.container.hl.ThirdHlMoReportResult;
import com.ty.modules.tunnel.send.container.entity.container.hl.ThirdHlSendReportResult;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by ljb on 2017/4/22 10:06.
 */
public class ThirdHlMessagerContainer extends AbstractThirdPartyMessageContainer {

    private static Logger logger= Logger.getLogger(ThirdHlMessagerContainer.class);

    private String url;
    private String userId;
    private String account;
    private String password;

    public ThirdHlMessagerContainer(Tunnel tunnel, MessageResponseEventProducer messageResponseEventProducer, MessageReportEventProducer messageReportEventProducer, MessageReplyEventProducer messageReplyEventProducer, MsgRecordService msgRecordService, MsgResponseService msgResponseService, MsgReportService msgReportService, MsgReplyService msgReplyService) {
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
        HttpResponse<String> result = null;
        try {
            result = Unirest.post(this.url+"/getfee")
                    .field("userid", this.userId)
                    .field("account",this.account)
                    .field("password",this.password)
                    .asString();
            String xmlResult = result.getBody();
            return Long.valueOf(xmlResult);
        } catch (UnirestException e) {
            return 0;
        }
    }

    @Override
    public void triggerMsgReportFetch() {
        try {

            String url = "http://111.204.226.241:7061/Status_customer_"+userId+".php?epid="+this.userId
                    +"&username="+this.account+"&password=5202135ee07b3432396de2f4f18042f6";
            HttpResponse<String> result = Unirest.get(url).asString();
            String res = result.getBody();
            if(StringUtils.isNotBlank(res)){
                logger.info("获取状态报告"+this.getTdName()+": result "+res);
            }
            if(StringUtils.isNotBlank(res) && !"1".equals(res) && !"epid error".equals(res)
                    && !"username or password error".equals(res)) {
                ThirdHlSendReportResult thirdHlSendReportResult = new ThirdHlSendReportResult(res);
                messageReportEventProducer.onData(thirdHlSendReportResult.getMsgReportList());
            }else{
                logger.info("返回状态报告为空");
            }
        } catch (Exception e) {
            logger.info("获取状态报告异常"+this.getTdName()+e.getMessage());
        }
    }

    @Override
    public void triggerMsgReplyFetch() {
        try {
            String url = "http://111.204.226.241:7061/Mo_customer_"+userId+".php?epid="+this.userId
                    +"&username="+this.account+"&password=5202135ee07b3432396de2f4f18042f6";
            HttpResponse<String> result = Unirest.get(url).asString();
            String res = result.getBody();
            if(StringUtils.isNotBlank(res)){
                logger.info("获取上行报告"+this.getTdName()+": result "+res);
            }
            if(StringUtils.isNotBlank(res) && !"1".equals(res) && !"epid error".equals(res)
                    && !"username or password error".equals(res)) {
                ThirdHlMoReportResult thirdHlMoReportResult = new ThirdHlMoReportResult(res);

                Map<String, MsgResponse> mrMap = msgResponseService.loadMsgResponseInfoMapByMsgIds(thirdHlMoReportResult.getAssocatedCustomerIds());

                List<MsgReply> msgReplyList = thirdHlMoReportResult.getMsgReplyList(mrMap);
                messageReplyEventProducer.onData(msgReplyList);
            }else{
                logger.info("返回上行报告为空");
            }
        } catch (Exception e) {
            logger.info("获取上行报告异常"+e.getMessage());
        }
    }

    @Override
    public boolean sendMsg(MessageSend ms) {
        ThirdMessageSendHl messageSend = (ThirdMessageSendHl) ms;
        String mobile = messageSend.getMobile();
        String content = messageSend.getContent();
        String msgId = IdGen.randomBase62(15);
        try {
            HttpResponse<String> result = Unirest.post(this.url)
                    .field("epid", this.userId)
                    .field("username", this.account)
                    .field("password", this.password)
                    .field("phone", mobile)
                    .field("message", Encodes.urlEncode(content,"gb2312"))
                    .field("linkid",msgId)
                    .field("subcode", messageSend.getSrcId()).asString();
            String xmlResult = result.getBody();
            logger.info(StringUtils.builderString(this.getTdName(), xmlResult));
            if(StringUtils.isNotBlank(xmlResult)) {
                messageSend.setRes(xmlResult);
                messageSend.setMsgId(msgId);
                messageResponseEventProducer.onData(messageSend.getMsgResponse());
                return true;
            }else  {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
