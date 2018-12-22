package com.ty.modules.tunnel.send.container.entity.container;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GetParamters {

    /**
     * 获得所有请求的参数，为空的不获得
     *
     * @param request
     */
    public static Map getParamters(HttpServletRequest request) {
        // 获取所有的请求参数
        Map properties = request.getParameterMap();
        // 返回值Map
        Map returnMap = new HashMap();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        //读取map中的值
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = " ";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            //将读取到的值存入map中
            returnMap.put(name, value);
            //移除map中为空的参数
            if ("".equals(value)) {
                returnMap.remove(name);
            }
        }
        return returnMap;
    }


}