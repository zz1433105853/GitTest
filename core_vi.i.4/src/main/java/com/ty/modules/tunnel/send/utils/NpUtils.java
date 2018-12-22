package com.ty.modules.tunnel.send.utils;

import com.google.common.collect.Maps;
import com.ty.common.utils.CacheUtils;
import com.ty.common.utils.SpringContextHolder;
import com.ty.common.utils.StringUtils;
import com.ty.modules.sys.service.NPService;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by Administrator on 2016/8/12.
 */
public class NpUtils {

    private static NPService npService = SpringContextHolder.getBean(NPService.class);
    private static final String NP_DATA_CACHE_MAP = "NP_CACHE_MAP";

    private static Logger logger= Logger.getLogger(NpUtils.class);


    public static boolean checkNp(){
        if(CacheUtils.get(NP_DATA_CACHE_MAP) == null){
            return true;
        }else{
            return false;
        }
    }

    public static void initNp() {
        Map<String, String> result = Maps.newHashMap();
        logger.info("查询NP....");
        String npStr = npService.loadAllNP();
        if(StringUtils.isNotBlank(npStr)) {
            String[] npStrArr = npStr.split(",");
            for(String s : npStrArr) {
                if(StringUtils.isNotBlank(s)) {
                    String[] inneNp = s.split("@");
                    if(inneNp!=null && inneNp.length==2 && StringUtils.isNotBlank(inneNp[0]) && StringUtils.isNotBlank(inneNp[1])) {
                        result.put(inneNp[0],inneNp[1]);
                    }
                }
            }
        }
        CacheUtils.put(NP_DATA_CACHE_MAP, result);
    }

    public static Map<String, String> getAllNpMap() {
        Map<String, String> result = (Map<String, String>) CacheUtils.get(NP_DATA_CACHE_MAP);
        if(result==null || result.size()==0) {
            result = Maps.newHashMap();
        }
        return result;
    }

}
