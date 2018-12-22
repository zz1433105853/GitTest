package com.ty.modules.tunnel.send.container.impl;

import com.google.common.collect.Maps;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.ty.common.mapper.JaxbMapper;
import com.ty.common.utils.MD5Utils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.sys.service.MsgRecordService;
import com.ty.modules.sys.service.MsgReplyService;
import com.ty.modules.sys.service.MsgReportService;
import com.ty.modules.sys.service.MsgResponseService;
import com.ty.modules.msg.entity.Tunnel;
import com.ty.modules.tunnel.reply.disruptor.MessageReplyEventProducer;
import com.ty.modules.tunnel.report.disruptor.MessageReportEventProducer;
import com.ty.modules.tunnel.response.disruptor.MessageResponseEventProducer;
import com.ty.modules.tunnel.send.container.entity.MessageSend;
import com.ty.modules.tunnel.send.container.entity.ThirdMessageSendMd;
import com.ty.modules.tunnel.send.container.entity.container.md.ThirdMdBalance;
import com.ty.modules.tunnel.send.container.entity.container.md.ThirdMdReportResult;
import com.ty.modules.tunnel.send.container.entity.container.md.ThirdMdSendMsgResult;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by Ysw on 2016/7/4.
 */
public class ThirdMdMessagerContainer extends AbstractThirdPartyMessageContainer{

    private static Logger logger= Logger.getLogger(ThirdMdMessagerContainer.class);

    private String url;
    private String account;
    private String password;

    public ThirdMdMessagerContainer(Tunnel tunnel,
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
        ThirdMessageSendMd messageSend = (ThirdMessageSendMd) ms;

        String mobile = messageSend.getMobile();
        String content = messageSend.getContent();
        try {
            HttpResponse<String> result = Unirest.post(this.url+"/mt")
                    .field("sn", this.account)
                    .field("pwd", MD5Utils.getMD5(this.account+this.password, "UTF-8").toUpperCase())
                    .field("mobile", mobile)
                    .field("content", content)
                    .field("rrid", "")
                    .field("msgfmt", "")
                    .field("ext", "")
                    .field("stime", "").asString();
            String xmlResult = result.getBody();
            logger.info(StringUtils.builderString(this.getTdName(), xmlResult));
            ThirdMdSendMsgResult thirdMdSendMsgResult = JaxbMapper.fromXml(xmlResult, ThirdMdSendMsgResult.class);
            if(thirdMdSendMsgResult!=null) {
                messageSend.setThirdMdSendMsgResult(thirdMdSendMsgResult);
                messageSend.setOriginStr(xmlResult);

                messageResponseEventProducer.onData(messageSend.getMsgResponse());
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 获取第三方余额
     * @return
     */
    @Override
    public long getBalance() {
        try {
            logger.info("MD5: "+ MD5Utils.getMD5(this.account+this.password, "UTF-8").toUpperCase());
            Map<String, Object> fields = Maps.newHashMap();
            fields.put("sn", this.account);
            fields.put("pwd", MD5Utils.getMD5(this.account+this.password, "UTF-8").toUpperCase());
            HttpResponse<String> result = Unirest.post(this.url+"/balance").fields(fields).asString();
            String xmlResult = result.getBody();
            ThirdMdBalance mb = JaxbMapper.fromXml(xmlResult, ThirdMdBalance.class);
            return Integer.valueOf(mb.getBalance());
        }catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void triggerMsgReportFetch() {
        try {
            HttpResponse<String> result = Unirest.post("http://report.entinfo.cn:8060/reportservice.asmx/report")
                    .field("sn", this.account)
                    .field("pwd", MD5Utils.getMD5(this.account+this.password, "UTF-8").toUpperCase())
                    .field("maxid", "1").asString();
            String xmlResult = result.getBody();
            logger.info(this.getTdName()+": result "+xmlResult);
            ThirdMdReportResult thirdMdReportResult = JaxbMapper.fromXml(xmlResult, ThirdMdReportResult.class);
            if(thirdMdReportResult!=null && StringUtils.isNotBlank(thirdMdReportResult.getReport()) &&
                    !"1".equals(thirdMdReportResult.getReport()) && !"-7".equals(thirdMdReportResult.getReport())) {
                messageReportEventProducer.onData(thirdMdReportResult.getMsgReportList());
            }else{
                logger.info("返回状态报告为空");
            }
        } catch (Exception e) {
            logger.info("获取状态报告异常"+e.getMessage());
        }
    }

    @Override
    public void triggerMsgReplyFetch() {

    }


}
