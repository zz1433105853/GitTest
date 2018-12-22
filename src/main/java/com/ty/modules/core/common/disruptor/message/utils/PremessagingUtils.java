package com.ty.modules.core.common.disruptor.message.utils;

import com.google.common.collect.Maps;
import com.ty.common.utils.CacheUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.Customer;
import com.ty.modules.msg.entity.Keywords;
import com.ty.modules.msg.entity.MessageSubmitLog;
import com.ty.modules.msg.entity.UserExtCode;
import com.ty.modules.msg.service.KeywordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/8/12.
 */
@Service
public class PremessagingUtils {
    private static  final String CACHE_KEYWORDS_INFO  = "keyWordsCache";
    @Autowired
    private KeywordsService keywordsService;

    /**
     * 验证是否包含非法关键字
     * @param customer
     * @param submitContent
     * @return
     */
    public boolean validationKeywords(Customer customer, String submitContent){
        Keywords keywords = null;
        if(StringUtils.isNotBlank(customer.getKeywordType())){
            for(String keywordsType : customer.getKeywordType().split(",")){
                keywords = (Keywords) CacheUtils.get(CACHE_KEYWORDS_INFO,keywordsType);
                if(keywords == null){
                   return false;
                }else{
                    for(String keywordsContent : keywords.getContent().split("\r\n")){
                        if(submitContent.indexOf(keywordsContent) != -1){
                            if(StringUtils.isBlank(customer.getDismissKeyword())){
                                return true;
                            }else{
                                boolean isDisMiss = false;
                                for(String disMiss:customer.getDismissKeyword().split("\r\n")){
                                    if(disMiss.equals(keywordsContent)){
                                        isDisMiss = true;
                                    }
                                }
                                if(!isDisMiss){
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }else{
            return false;
        }
        return false;
    }

    /**
     * 验证签名
     * @param customer
     * @param content
     * @return
     */
    public boolean validationSign(Customer customer,String content){
        String sign = getSign(content);
        if(StringUtils.isBlank(sign)){
            return true;
        }else if("1".equals(customer.getIsLimitSign()) && !sign.equals(customer.getSignature())){
            return true;
        }
        return false;
    }

    /**
     * SDK验证签名（扩展号）
     * @param customer
     * @param messageSubmitLog
     * @param
     * @return
     */
    public boolean validationSignSDK(Customer customer,MessageSubmitLog messageSubmitLog){
        String sign = getSign(messageSubmitLog.getContent());
        if(StringUtils.isBlank(sign)){
            return true;
        }else if("1".equals(customer.getIsLimitSign())){
            if(StringUtils.isBlank(messageSubmitLog.getExtCode())){
                if(!sign.equals(customer.getSignature())){
                    return true;
                }
            }else{
                if(!sign.equals(customer.getSignature()) && !sign.equals(extCodeMap(customer.getUserExtCodeList()).get(messageSubmitLog.getExtCode()))){
                    return true;
                }
            }
        }
        return false;
    }

    public String modifyContentToSignByIsp(Customer customer,MessageSubmitLog messageSubmitLog){
        String thisSign = "";
        //String content = "测试发送内容【2017】发【和和】送。【test】";
        String content = messageSubmitLog.getContent();
        if("1".equals(customer.getIsLimitSign())){
            String re = "【([^】]+)】";
            Pattern p = Pattern.compile(re);
            Matcher m = p.matcher(content);
            while(m.find()){
                String s = m.group(1);
                    if(StringUtils.isBlank(messageSubmitLog.getExtCode())){
                        if(s.equals(customer.getSignature())){
                            thisSign = customer.getSignature();
                        }
                    }else{
                        if(s.equals(customer.getSignature()) ){
                            thisSign = customer.getSignature();
                        }
                        String extCodeSign = extCodeMap(customer.getUserExtCodeList()).get(messageSubmitLog.getExtCode());
                        if(s.equals(extCodeSign)){
                            thisSign = extCodeSign;
                        }
                    }
            }
            if(StringUtils.isBlank(thisSign)){
                content = content.replace("【"+thisSign+"】","").replace("【","[").replace("】","]");
            }else{
                content = "【"+thisSign+"】"+content.replace("【"+thisSign+"】","").replace("【","[").replace("】","]");
            }
        }else{
            String[] str = content.split("【");
            if(str.length ==2){
                String sign = getSign(messageSubmitLog.getContent());
                content = "【"+sign+"】"+content.replace("【"+sign+"】","");
            }else{
                content = null;
            }
        }
        return content;
    }

    private Map<String,String> extCodeMap(List<UserExtCode> userExtCodeList){
        Map<String,String> resultMap = Maps.newHashMap();
        for(UserExtCode userExtCode:userExtCodeList){
            resultMap.put(userExtCode.getExtCode(),userExtCode.getSignature());
        }
        return resultMap;
    }

    /**
     * 根据提交内容获取签名内容
     * @param content
     * @return
     */
    public String getSign(String content){
        int start = content.indexOf("【");
        int end = content.indexOf("】");
        if(start == -1){
            return "";
        }else if(end == -1){
            return "";
        }else{
            return content.substring(start+1,end);
        }

    }


}
