package com.ty.modules.tunnel.send.container.impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.ty.common.mapper.JaxbMapper;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.Tunnel;
import com.ty.modules.sys.service.MsgRecordService;
import com.ty.modules.sys.service.MsgReplyService;
import com.ty.modules.sys.service.MsgReportService;
import com.ty.modules.sys.service.MsgResponseService;
import com.ty.modules.tunnel.reply.disruptor.MessageReplyEventProducer;
import com.ty.modules.tunnel.report.disruptor.MessageReportEventProducer;
import com.ty.modules.tunnel.response.disruptor.MessageResponseEventProducer;
import com.ty.modules.tunnel.send.container.entity.MessageSend;
import com.ty.modules.tunnel.send.container.entity.container.qxt.ThirdQxt;
import com.ty.modules.tunnel.send.container.entity.container.qxt.ThirdQxtSendMsgresult;
import org.apache.log4j.Logger;

/**
 * 企信通
 * Created by ljb on 2017/4/22 10:06.
 */
public class ThirdQxtMessagerContainer extends AbstractThirdPartyMessageContainer {

    private static Logger logger = Logger.getLogger(ThirdQxtMessagerContainer.class);

    private String url;
    private String account;
    private String password;

    public ThirdQxtMessagerContainer(Tunnel tunnel, MessageResponseEventProducer messageResponseEventProducer, MessageReportEventProducer messageReportEventProducer, MessageReplyEventProducer messageReplyEventProducer, MsgRecordService msgRecordService, MsgResponseService msgResponseService, MsgReportService msgReportService, MsgReplyService msgReplyService) {
        super(tunnel, messageResponseEventProducer, messageReportEventProducer, messageReplyEventProducer, msgRecordService, msgResponseService, msgReportService, msgReplyService);
        if (tunnel != null) {
            this.url = tunnel.getUrl();
            this.account = tunnel.getAccount();
            this.password = tunnel.getPassword();
        }
    }

    @Override
    public long getBalance() {
        return 0L;
    }

    @Override
    public void triggerMsgReportFetch() {
    }

    @Override
    public void triggerMsgReplyFetch() {
    }

    @Override
    public boolean sendMsg(MessageSend ms) {
        ThirdQxtSendMsgresult messageSend = (ThirdQxtSendMsgresult) ms;
        String mobile = messageSend.getMobile();
        String content = messageSend.getContent();
        try {
            String sendUrl = this.url + "/WebAPI/SmsAPI.asmx/SendSmsExt";
            String sendAccount = this.account;
            String sendPassword  =this.password;
            HttpResponse<String> result = Unirest.post(sendUrl)
                    .field("user", sendAccount)
                    .field("pwd", sendPassword)
                    .field("mobiles", mobile)
                    .field("chid", "0")
                    .field("sendtime", "")
                    .field("contents", content).asString();
            String resultTunnel = result.getBody();
            logger.info(StringUtils.builderString("发送企信通参数","sendUrl->",sendUrl,",sendAccount->",sendAccount,",pwd->",sendPassword,
                    ",mobile->"+mobile,",child","0",",sendTime->","",",contents->"+content));
            logger.info(StringUtils.builderString(this.getTdName(),"发送短信返回结果" + resultTunnel));
            if (StringUtils.isNotBlank(resultTunnel)) {
                ThirdQxt thirdQxt = JaxbMapper.fromXml(resultTunnel, ThirdQxt.class);
                messageSend.setRemarks(resultTunnel);
                messageSend.setThirdQxt(thirdQxt);
                messageResponseEventProducer.onData(messageSend.getMsgResponse());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("企信通发送异常"+e.getMessage());
            return false;
        }
    }

}
