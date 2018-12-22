package com.ty.modules.msg.cxf.impl;

import com.ty.common.config.Global;
import com.ty.common.security.Digests;
import com.ty.common.utils.CacheUtils;
import com.ty.common.utils.Encodes;
import com.ty.common.utils.StringUtils;
import com.ty.modules.core.common.disruptor.message.MessageCore;
import com.ty.modules.core.common.disruptor.message.MessageEventProducer;
import com.ty.modules.core.common.disruptor.message.ProductCode;
import com.ty.modules.msg.cxf.MessageResult;
import com.ty.modules.msg.cxf.MessageWebService;
import com.ty.modules.msg.entity.Customer;
import com.ty.modules.msg.entity.MessageSubmitLog;
import com.ty.modules.msg.entity.UserSubmit;
import com.ty.modules.msg.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 * Created by Ysw on 2016/5/30.
 */
@Component
@WebService(targetNamespace = "http://service.com/", endpointInterface = "com.ty.modules.msg.cxf.MessageWebService")
public class MessageWebServiceImpl implements MessageWebService {
    public static final int HASH_INTERATIONS = 1024;
    public static final int SALT_SIZE = 8;
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageEventProducer messageEventProducer;
    @Autowired
    private CustomerService customerService;
    @Resource
    private WebServiceContext wsContext;

    @Override
    public String sendMsg(@QueryParam("sn") String sn, @QueryParam("password") String password,@QueryParam("mobile") String mobile
        ,@QueryParam("content") String content
        ,@QueryParam("ext") String ext
        ,@QueryParam("sendTime") String sendTime) {
        String result = "";
        if(StringUtils.isBlank(sn)
                || StringUtils.isBlank(password)
                || StringUtils.isBlank(mobile)
                || StringUtils.isBlank(content)){
            result = MessageResult.LACK_OF_PARAM;
        }else{
            UserSubmit userSubmit = new UserSubmit();
            userSubmit.setSn(sn);
            userSubmit.setPassword(password);
            userSubmit.setMobile(mobile);
            userSubmit.setContent(content);
            userSubmit.setExt(ext);
            userSubmit.setSendTime(sendTime);
            Customer customer = (Customer) CacheUtils.get(Global.CACHE_CUSTOMER_INFO,sn);
            int toFeeCount = getPayCount(mobile,content);//扣费条数
            userSubmit.setToFeeCount(toFeeCount);
            if (customer == null){
                result = MessageResult.ACCOUNT_NOT_EXIST;
            }else if(!validatePassword(userSubmit.getPassword(), customer.getPassword())) {
               result = MessageResult.ACCOUNT_PASSWORD_ERROR;
            }else if(!"sdk".equals(customer.getAccessType())){
                result = MessageResult.NOT_SDK_TYPE;
            } else if(!"1".equals(customer.getIsSupportAfterPay()) && customer.getRestCount() < toFeeCount){
                result = MessageResult.ACCOUNT_BALANCE_LACK;
            }else if(validIp(customer)){
                result = MessageResult.IP_AUTH_ERROR;
            }else{
                int mobileLength = userSubmit.getMobile().split(",").length;
                if(mobileLength > Global.GETCOUNT){
                    result = MessageResult.SUBMIT_MOBILE_TOO_MUCH;
                }else{//提交成功
                    MessageSubmitLog messageSubmitLog = new MessageSubmitLog();
                    messageSubmitLog.preInsert();
                    messageSubmitLog.setIp(getIpAddr());
                    MessageCore messageCore = new MessageCore(ProductCode.PRE_MESSAGE,customer,userSubmit,messageSubmitLog);
                    messageEventProducer.onData(messageCore);
                    result = messageSubmitLog.getId();
                }
            }
        }
        return result;
    }

    /**
     * 获取扣费条数
     * @param mobile：手机号，多个按逗号隔开
     * @param content：短信内容
     * @return
     */
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

    /**
     * 验证密码
     * @param plainPassword 明文密码
     * @param password 密文密码
     * @return 验证成功返回true
     */
    public static boolean validatePassword(String plainPassword, String password) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Encodes.decodeHex(password.substring(0,16));
        byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
        return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
    }
    /**
     * 验证SDK客户发送短信IP鉴权
     * @param customer
     * @return
     */
    private boolean validIp(Customer customer){
        String ip = getIpAddr();
        logger.info("客户请求ip=====>"+ip);
        if(StringUtils.isNotBlank(customer.getIp()) && !StringUtils.inString(ip,customer.getIp().split(","))){
            return true;
        }
        return false;
    }

    private String getIpAddr() {
        MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest request = (HttpServletRequest)(mc.get(MessageContext.SERVLET_REQUEST));
        return request.getRemoteAddr();
    }
}
