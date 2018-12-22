package com.ty.modules.tunnel.send.container.impl;

import com.google.common.collect.Lists;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.ty.common.config.Global;
import com.ty.common.mapper.JaxbMapper;
import com.ty.common.utils.DateUtils;
import com.ty.common.utils.JedisUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.ArrivedStatusReport;
import com.ty.modules.msg.entity.MsgReport;
import com.ty.modules.msg.entity.Tunnel;
import com.ty.modules.msg.service.SmsSendLogService;
import com.ty.modules.sys.service.MsgRecordService;
import com.ty.modules.sys.service.MsgReplyService;
import com.ty.modules.sys.service.MsgReportService;
import com.ty.modules.sys.service.MsgResponseService;
import com.ty.modules.tunnel.reply.disruptor.MessageReplyEventProducer;
import com.ty.modules.tunnel.report.disruptor.MessageReportEventProducer;
import com.ty.modules.tunnel.response.disruptor.MessageResponseEventProducer;
import com.ty.modules.tunnel.send.container.entity.MessageSend;
import com.ty.modules.tunnel.send.container.entity.ThirdMessageSendklws;
import com.ty.modules.tunnel.send.container.entity.container.klws.Arrivedklws;
import com.ty.modules.tunnel.send.container.entity.container.klws.FetchReportParaRedisKlws;
import com.ty.modules.tunnel.send.container.entity.container.klws.ThirdKlwsResultCode;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ThirdKlwsMessagerContainer extends AbstractThirdPartyMessageContainer {

    private static Logger logger= Logger.getLogger(ThirdKlwsMessagerContainer.class);
    @Autowired
    private SmsSendLogService smsSendLogService;
    private String url;
    private String userId;
    private String account;
    private String password;

    public ThirdKlwsMessagerContainer(Tunnel tunnel,
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
    public boolean sendMsg(MessageSend ms){
        ThirdMessageSendklws messageSend = (ThirdMessageSendklws) ms;
        String mobile = messageSend.getMobile();
        String sendcontent = messageSend.getContent();

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);

        // 设置post参数
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("ua", this.account));
        parameters.add(new BasicNameValuePair("pw", this.password));
        parameters.add(new BasicNameValuePair("mb", mobile));
        parameters.add(new BasicNameValuePair("ms", sendcontent));
        parameters.add(new BasicNameValuePair("ex", messageSend.getSrcId()));
        parameters.add(new BasicNameValuePair("dm", ""));
        // 构造一个form表单式的实体
        UrlEncodedFormEntity formEntity = null;
        try {
            formEntity = new UrlEncodedFormEntity(parameters);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 将请求实体设置到httpPost对象中
        httpPost.setEntity(formEntity);
        // 伪装浏览器请求
        httpPost.setHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; Win64; x64)" +
                        " AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");

        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpclient.execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                // 获取服务端响应的数据
                String content = EntityUtils.toString(response.getEntity(),
                        "UTF-8");
                System.out.println("快乐网视——获取到的响应："+content);
                //ThirdKlwsResultCode thirdKlwsResultCode =new ThirdKlwsResultCode();
                ThirdKlwsResultCode thirdKlwsResultCode=SthToKlws(content);//获取到的内容转化为对象
                logger.info("消息状态:"+thirdKlwsResultCode.getStatus());
                logger.info("消息id:"+thirdKlwsResultCode.getTaskid());
                if(thirdKlwsResultCode!=null) {
                    messageSend.setThirdKlwsResultCode(thirdKlwsResultCode);
                    messageSend.setOriginStr(content);
                    logger.debug("-wsy--klws获取到的应答"+messageSend.getMsgResponse());
                    messageResponseEventProducer.onData(messageSend.getMsgResponse());
                    return true;
                }else {
                    return false;
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
    //@Override
    public boolean sendMsg_yuan(MessageSend ms) {
        ThirdMessageSendklws messageSend = (ThirdMessageSendklws) ms;
        String mobile = messageSend.getMobile();
        String content = messageSend.getContent();
        try {
            //String sendUrl = "http://101.251.236.60:18002/?ua=sxty-lt-dc&pw=195889&mb=18535837246&ms=【天元科技】1507&ex=000507&dm=";
            String sendUrl = url+"/?ua="+this.account+"&pw="+this.password+"&mb="+mobile+"&ms="+content+"&ex="+messageSend.getSrcId()+"&dm=";
            logger.debug(sendUrl);
            HttpResponse<String> result = Unirest.get(sendUrl).asString();
/*            HttpResponse<String> result = Unirest.post(url)
                    .field("ua",this.account)
                    .field("pw",this.password)
                    .field("mb",mobile)
                    .field("ms",content)
                    .field("ex",messageSend.getSrcId())
                    .field("tm","20181109153600")
                    .field("dm","").asString();*/
            String xmlResult = result.getBody();
             logger.debug("-wsy--klws获取到的发送应答内容"+xmlResult);
            ThirdKlwsResultCode thirdKlwsResultCode = JaxbMapper.fromXml(xmlResult,ThirdKlwsResultCode.class);
            if(thirdKlwsResultCode!=null) {
                messageSend.setThirdKlwsResultCode(thirdKlwsResultCode);
                messageSend.setOriginStr(xmlResult);
                logger.debug("-wsy--klws获取到的应答"+messageSend.getMsgResponse());
                messageResponseEventProducer.onData(messageSend.getMsgResponse());
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

//测试
    public boolean sendMsgCs() {
       DateUtils dateUtils =new DateUtils();
       String tm = dateUtils.getDate("yyyyMMddHHmmss");
        //String sendUrl = "http://101.251.236.60:18002/?ua=sxty-lt-dc&pw=195889&mb=18535837246&ms=【天元科技】1507&ex=000507&dm=";
        try {
            HttpResponse<String> result = Unirest.post("http://101.251.236.60:18002")
                    .field("ua","sxty-lt-dc")
                    .field("pw","195889")
                    .field("mb","18525837246")
                    .field("ms","【天元科技】"+tm)
                    .field("ex","000507")
                    .field("tm",tm)
                    .field("dm","").asString();
            String xmlResult = result.getBody();
            logger.info(StringUtils.builderString(this.getTdName(), xmlResult));
            logger.debug("-wsy--klws获取到的发送应答内容"+xmlResult);
            ThirdKlwsResultCode thirdKlwsResultCode = JaxbMapper.fromXml(xmlResult,ThirdKlwsResultCode.class);
            return true;
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
/*        HttpResponse<String> result = null;
        try {
            result = Unirest.post("http://101.251.236.60:18005/balance.do?ua=sxty-lt-dc&pw=195889").asString();
            String xmlResult = result.getBody();
            ThirdklwsResultBalanceCode thirdTxctSendMsgResult = JaxbMapper.fromXml(xmlResult, ThirdklwsResultBalanceCode.class);
            return thirdTxctSendMsgResult.getData();
        } catch (UnirestException e) {*/
            return 0;
       // }
    }

    @Override

    public void triggerMsgReportFetch() {
        long end1 = System.currentTimeMillis();
        StringBuilder result = null;
        List<String> listString = new ArrayList<String>();
        try {
            List<Arrivedklws> arrivedklwsList  = convertToClass();//从redis获取数据
            List<MsgReport> msgReports=getMsgReportList(arrivedklwsList);//封装成状态报告

            if (!msgReports.isEmpty()) {
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
    public void triggerMsgReplyFetch() {

    }
    //字符串转化为对象
    public static  ThirdKlwsResultCode SthToKlws(String result)
    {
        ThirdKlwsResultCode thirdKlwsResultCode =new ThirdKlwsResultCode();
        String[] sourceStrArray = result.split(",");
        for (int i = 0; i < sourceStrArray.length; i++) {
            thirdKlwsResultCode.setStatus(sourceStrArray[0]);
            thirdKlwsResultCode.setTaskid(sourceStrArray[1]);
        }
        return thirdKlwsResultCode;
    }

    public List<Arrivedklws> convertToClass() {

        long listLen = JedisUtils.llen(Global.FTCHREPORTKLWS_REDIS_KEY);

        if (listLen > 0 && Global.REDIS_GET_SWITCH == 1) {

            if (listLen >  100) {
                listLen = 100;
            }
            List<Object> list = JedisUtils.getObjectList(Global.FTCHREPORTKLWS_REDIS_KEY, 0, (listLen - 1));//从redis中获取对象
            JedisUtils.lTrim(Global.FTCHREPORTKLWS_REDIS_KEY, listLen, -1);//截取偏移量范围内的list
            try{
                if (!list.isEmpty()) {
                    List<Arrivedklws> result = Lists.newArrayList();
                    for (Object obj : list) {
                        if (obj instanceof FetchReportParaRedisKlws) {
                            FetchReportParaRedisKlws m = (FetchReportParaRedisKlws) obj;
                            Arrivedklws arrivedklws = new Arrivedklws();
                            arrivedklws.setMsgid(m.getMsgid());
                            arrivedklws.setMobile(m.getMobile());
                            arrivedklws.setStat(m.getStat());
                            arrivedklws.setArrivedTime(m.getArrivedTime());
                            result.add(arrivedklws);
                        }
                    }
                    list.clear();
                    return result;
                }
            }
            catch (Exception e) {
                JedisUtils.listObjectAdd(Global.FTCHREPORTKLWS_REDIS_KEY, list);
                logger.error("\r(o)(o) - - 执行失败, - - (o)(o)\r" + e.getMessage(), e);
            }
        }
        return Collections.EMPTY_LIST;
    }
    /*
    * 封装为MsgReport
* */
    public List<MsgReport> getMsgReportList(List<Arrivedklws> arrivedklws){
        List<MsgReport> result = Lists.newArrayList();
        if(arrivedklws!=null){
            for(Arrivedklws arrivedStatusReport:arrivedklws){
                MsgReport msgReport = new MsgReport();
                msgReport.setMsgId(arrivedStatusReport.getMsgid());
                msgReport.setMobile(arrivedStatusReport.getMobile());
/*                    msgReport.setArrivedStatus(arrivedStatusReport.getArrivedStatus()=="0"?"1":"0");
                    msgReport.setArrivedResultMessage((arrivedStatusReport.getArrivedStatus()=="1")?"DELIVRD":"NO_DELIVRD");*/
                msgReport.setArrivedStatus("1");
                msgReport.setArrivedResultMessage("DELIVRD");
                msgReport.setArrivedTime(DateUtils.parseDate(arrivedStatusReport.getArrivedTime()));
                result.add(msgReport);

            }
        }
        return result;
    }

}





