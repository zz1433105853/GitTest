package com.ty.modules.tunnel.send.utils;


import com.ty.common.utils.CacheUtils;
import com.ty.common.utils.SpringContextHolder;
import com.ty.modules.sys.entity.Dm;
import com.ty.modules.sys.service.DmService;

import java.util.List;

/**
 * 加载手机号归属地信息
 * Created by tykfkf02 on 2017/4/5.
 */
public class DmUtils {

    private static DmService dmService = SpringContextHolder.getBean(DmService.class);

    /**
     * 加载手机号码归属地信息
     */
    public static void loadAllDm(){
        List<Dm> dms = dmService.findList(new Dm());
        for(Dm dm:dms){
            CacheUtils.put(dm.getMobileNumber(),"sx");
        }
    }
}
