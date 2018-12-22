package com.ty.modules.msg.entity;

import com.ty.common.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by tykfkf02 on 2016/12/20.
 */
public class HttpParamUtility {
    private HashMap<String,String> params;

    /*
     * Tomcat设置URIEncoding默认编码为UTF-8时正确解析GBK参数
     * @param req
     */
    public HttpParamUtility(HttpServletRequest req) {
        try{
            //只解析GET提交的数据
            if("GET".equals(req.getMethod())){
                params = new HashMap<String, String>();
                //解析queryString
                String queryString = req.getQueryString();
                String[] ps = queryString.split("&");
                String[] p = null;
                for(String param : ps) {
                    if(StringUtils.isBlank(param) || !param.contains("=")) continue;
                    p = param.split("=");
                    if(p.length<1) {continue;
                    } else if(p.length==1) {
                        params.put(p[0], "");
                    } else {
                        p[1] = p[1]==null?"":p[1];
                        params.put(p[0], URLDecoder.decode(p[1], "GBK"));
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getParameter(String key) {
        return params != null ? params.get(key) : "";
    }
}
