package com.ty.modules.tunnel.send.container.cx.impl;

import com.google.common.collect.Maps;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.ty.common.mapper.JaxbMapper;
import com.ty.common.utils.*;
import com.ty.modules.msg.entity.CxTunnel;
import com.ty.modules.msg.entity.MsgCxRecord;
import com.ty.modules.msg.entity.MsgCxResponse;
import com.ty.modules.sys.service.MsgReportService;
import com.ty.modules.sys.service.MsgResponseService;
import com.ty.modules.tunnel.send.container.entity.container.md.ThirdMdBalance;
import com.ty.modules.tunnel.send.container.entity.container.md.ThirdMdReportResult;
import com.ty.modules.tunnel.send.container.entity.container.md.ThirdMdSendMsgResult;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Ysw on 2016/7/4.
 */
public class ThirdCxMdMessagerContainer extends AbstractThirdCxPartyMessageContainer {

    private static Logger logger= Logger.getLogger(ThirdCxMdMessagerContainer.class);
    private static PropertiesLoader loader = new PropertiesLoader("system.properties");

    private String url;
    private String account;
    private String password;

    public ThirdCxMdMessagerContainer(
                                      CxTunnel cxTunnel,
                                      MsgResponseService msgResponseService,
                                      MsgReportService msgReportService
    ) {
        super(cxTunnel,msgResponseService, msgReportService);
        if(cxTunnel!=null) {
            this.url = cxTunnel.getUrl();
            this.account = cxTunnel.getAccount();
            this.password = cxTunnel.getPassword();
        }
    }

    @Override
    public boolean sendCx(MsgCxRecord ms) {
        String mobile = ms.getMobile();
        String content = ms.getContent();
        String title = ms.getTitle();
        try {
            String sendContent = generateContent(content);
            logger.info(getTdName()+"生成发送彩信内容："+sendContent);
            if(StringUtils.isNotBlank(sendContent)){
                sendContent = sendContent.substring(0,sendContent.length()-1);
                logger.info(getTdName()+"发送彩信内容："+sendContent);
                HttpResponse<String> result = Unirest.post(url+"/mdMmsSend")
                        .field("sn", this.account)
                        .field("pwd", MD5Utils.getMD5(this.account + this.password, "UTF-8").toUpperCase())
                        .field("mobile", mobile)
                        .field("content", sendContent)
                        .field("title", title)
                        .field("stime", "").asString();
                String xmlResult = result.getBody();
                logger.info(StringUtils.builderString(this.getTdName(),"彩信返回结果", xmlResult));
                ThirdMdSendMsgResult thirdMdSendMsgResult = JaxbMapper.fromXml(xmlResult, ThirdMdSendMsgResult.class);
                MsgCxResponse msgCxResponse = new MsgCxResponse();
                msgCxResponse.setCxTunnel(getTunnel());
                msgCxResponse.setMsgCxRecord(ms);
                msgCxResponse.setMsgId(thirdMdSendMsgResult.getRrid());
                msgCxResponse.setSendResultMessage(thirdMdSendMsgResult.toString());
                msgCxResponse.setSendStatus(thirdMdSendMsgResult.isSuccess()?"1":"2");
                List<MsgCxResponse> msgResponseList =  msgCxResponse.toMsgResponseList();
                if(!msgResponseList.isEmpty()){
                    StringBuilder responseSql = msgCxResponse.generateSql(msgResponseList);
                    msgResponseService.batchInsert(responseSql);
                }
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            logger.info(StringUtils.builderString(getTdName(),"发送彩信异常：",e.getMessage()));
            return false;
        }
    }


    private String generateContent(String content) throws Exception{
        StringBuffer sendContent = new StringBuffer();
        String[] contentSplit = content.split("////");
        String domainName = loader.getProperty("domain_name");
        for(int i=1;i<=contentSplit.length;i++){
            String[] c = contentSplit[i-1].split("##");
            if(!"null".equals(c[0])){
                String separator = File.separator;
                String savePath = separator+"storage"+separator+ IdGen.randomBase62(3)+"-temp.txt";
                Encodes.writeStringToFile(savePath,c[0]);
                String txtBase64 = Encodes.encodeBase64File(savePath);
                sendContent.append(i).append("_1.txt,").append(txtBase64).append(";");
            }
            if(!"null".equals(c[1])){
                String prefix=c[1].substring(c[1].lastIndexOf(".")+1);
                String imgUrl = domainName+c[1];
                logger.info("远程图片地址"+imgUrl);
                String imgBase64 = Encodes.getURLImage(imgUrl);
                sendContent.append(i).append("_2.").append(prefix).append(",").append(imgBase64).append(";");
            }
            if(!"null".equals(c[2])){
                String prefix=c[2].substring(c[2].lastIndexOf(".")+1);
                String syUrl = domainName+c[2];
                logger.info("远程声音地址"+syUrl);
                String syBase64 = Encodes.getURLImage(syUrl);
                sendContent.append(i).append("_3.").append(prefix).append(",").append(syBase64).append(";");
            }
        }
        return sendContent.toString();
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
