package com.ty.modules.sys.common;

import com.ty.common.config.Global;
import com.ty.common.utils.CacheUtils;
import com.ty.modules.msg.entity.Template;
import com.ty.modules.msg.service.TemplateService;
import com.ty.modules.tunnel.send.utils.DmUtils;
import com.ty.modules.tunnel.send.utils.NpUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by tykfkf02 on 2017/3/25.
 * 初始化系统缓存数据
 */
@Service
public class StartAddDataListener implements ApplicationListener<ContextRefreshedEvent>
{
    private static Logger logger= Logger.getLogger(StartAddDataListener.class);

    @Value("${cache.np}")
    private boolean cacheNp;

    @Value("${cache.dm}")
    private boolean cacheDm;

    @Autowired
    private TemplateService templateService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        if(event.getApplicationContext().getParent() == null) {
            try{
                if(cacheNp) {
                    logger.info("===============启动加载NP信息放到缓存开始==========================");
                    if(NpUtils.checkNp()) {
                        NpUtils.initNp();
                    }
                    logger.info("===============启动加载NP信息放到缓存结束==========================");
                }
                if(cacheDm) {
                    logger.info("===============启动加载手机号区域信息放到缓存开始==========================");
                    DmUtils.loadAllDm();
                    logger.info("===============启动加载手机号区域信息放到缓存结束==========================");
                }
                logger.info("===============启动加载客户短信模板信息放到缓存开始==========================");
                Template tForQuery = new Template();
                tForQuery.setStatus("1");
                List<Template> tList = templateService.findList(tForQuery);
                for(Template t : tList) {
                    if(t==null) continue;
                    CacheUtils.put(Global.CACHE_TPL_INFO,t.getCustomer().getId(),t);
                }
                logger.info("===============启动加载客户短信模板信息放到缓存结束==========================");
            }catch (Exception e){
                logger.error("startAddData异常"+e.getMessage());
            }

        }
    }
}
