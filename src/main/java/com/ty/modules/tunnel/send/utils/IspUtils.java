package com.ty.modules.tunnel.send.utils;



import com.google.common.collect.Maps;
import com.ty.common.utils.StringUtils;

import java.util.Map;

/**
 * Created by tykfkf02 on 2016/7/1.
 */
public class IspUtils {
    public static Map<String,String> isp = Maps.newHashMap();
    /**
     * 1: 移动 2: 联通 3: 电信
     */
    static{
        isp.put("134","1");isp.put("135","1");isp.put("136","1");isp.put("137","1");isp.put("138","1");isp.put("139","1");
        isp.put("140","1");isp.put("150","1");isp.put("151","1");isp.put("152","1");isp.put("157","1");isp.put("158","1");
        isp.put("159","1");isp.put("182","1");isp.put("183","1");isp.put("184","1");isp.put("187","1");isp.put("188","1");
        isp.put("147","1");isp.put("178","1");isp.put("1705","1");isp.put("198","1");isp.put("1709","2");isp.put("130","2");isp.put("131","2");
        isp.put("132","2");isp.put("155","2");isp.put("156","2"); isp.put("166","2");isp.put("185","2");isp.put("186","2");isp.put("145","2");
        isp.put("171","2");isp.put("175","2");isp.put("176","2");isp.put("1707","2");isp.put("1708","2");isp.put("133","3");
        isp.put("153","3"); isp.put("180","3");isp.put("181","3"); isp.put("189","3");isp.put("1700","3");isp.put("177","3");
        isp.put("173","3");isp.put("149","3");isp.put("199","3");
    }
    /**
     * 根据手机号前三位获取isp类型： 1: 移动 2: 联通 3: 电信
     * @param mobile
     * @return
     */
    public static String getIspTypeByTopThree(String mobile){
        if(StringUtils.isBlank(mobile) || mobile.length()!=11) return "";
        String top = mobile.substring(0,3);
        if("170".equals(top)){
            top = mobile.substring(0,4);
        }
        String result = isp.get(top);
        return result == null ?"":result;
    }
}
