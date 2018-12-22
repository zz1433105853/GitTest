package com.ty.modules.msg.job;

import com.ty.common.config.Global;
import com.ty.common.utils.CacheUtils;
import com.ty.common.utils.JedisUtils;
import com.ty.common.utils.StringUtils;
import com.ty.modules.msg.entity.BlackWhiteList;
import com.ty.modules.msg.entity.Customer;
import com.ty.modules.msg.entity.Keywords;
import com.ty.modules.msg.service.BlackWhiteListService;
import com.ty.modules.msg.service.KeywordsService;
import com.ty.modules.sys.entity.Dict;
import com.ty.modules.sys.service.DictService;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 刷新关键字，黑白名单缓存任务
 * Created by ljb on 2016/7/7.
 */

@Component
public class RefreshKeyWordBlackWhiteCacheJob extends QuartzJobBean {
    @Autowired
    private DictService dictService;
    @Autowired
    private KeywordsService keywordsService;
    @Autowired
    private BlackWhiteListService blackWhiteListService;
    private Logger logger = Logger.getLogger(getClass());
    public void refreshKeyWordBlackWhiteCacheJob() {
        logger.info("\r(o)(o) - - - - - - - - - - - - - (o)(o)\r");
        logger.info("\r(o)(o) - -  刷新关键字，黑白名单缓存任务- - (o)(o)\r");
        logger.info("\r(o)(o) - - - - - - - - - - - - - (o)(o)\r");
        try{
            Dict dict = new Dict();
            dict.setType("keywords_type");
            List<Dict> keyWordDicts = dictService.findList(dict);
            for(Dict k:keyWordDicts){
                Keywords keywords = new Keywords();
                keywords.setType(k.getValue());
                keywords = keywordsService.get(keywords);
                if(keywords != null){
                    CacheUtils.put(Global.CACHE_KEYWORDS_INFO,k.getValue(),keywords);
                }
            }

            //平台黑白名单
            List<BlackWhiteList> blackWhiteLists = blackWhiteListService.findPlatformList();
            if(!blackWhiteLists.isEmpty()){
                CacheUtils.put(Global.CACHE_BLACK_INFO,"platform",blackWhiteLists);
            }
            //客户黑名单
            BlackWhiteList blackWhiteList = new BlackWhiteList();
            blackWhiteList.setType("black");
            List<Customer> customerList = blackWhiteListService.findAllCustomerList(blackWhiteList);
            if(!customerList.isEmpty()){
                for(Customer customer:customerList){
                    if(!customer.getBlackWhiteListList().isEmpty()){
                        CacheUtils.put(Global.CACHE_CUSTOMER_BLACK_INFO,customer.getId(),customer.getBlackWhiteListList());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("刷新关键字，黑白名单缓存任务 - Internal");
    }
}
