package com.ty.modules.msg.web;

import com.google.common.collect.Maps;
import com.ty.common.JavaBeanUtil;
import com.ty.common.config.Global;
import com.ty.common.mapper.JsonMapper;
import com.ty.common.utils.DateUtils;
import com.ty.common.utils.JedisUtils;
import com.ty.common.web.BaseController;

import com.ty.modules.msg.entity.ResponseCode;
import com.ty.modules.msg.entity.Status;
import com.ty.modules.tunnel.send.container.entity.container.MapToClass;
import com.ty.modules.tunnel.send.container.entity.container.tykj.FetchReportParaRedisTy;
import com.ty.modules.tunnel.send.container.entity.container.tykj.ThirdTykjReportResult;
import com.ty.modules.tunnel.send.container.entity.container.tykj.ThirdTykjReportSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping ("msgTy")
public class MessageTyTunelController extends BaseController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    HttpServletRequest request;
    /**
     * 获取到的参数转化为一个对象列表类
     *
     * @param request
     * @return
     */
    @RequestMapping("generateStatusReport")
    @ResponseBody
    public List<ThirdTykjReportSimple> generateStatusReport(HttpServletRequest request) {

        Map<String, Object> map = getParamters(request);

        //简单对象
        MapToClass testData = (MapToClass) JavaBeanUtil.convertMapToBean(MapToClass.class, map);//map转成一个类，key的值是类的属性值，可能是各种格式的字符串，例如：json 拼接的串
        //拼接串
        String str = "{\"data\":" + testData.getData() + "}";
        // String str = testData.getData();
       // String str ="{\"data\":[{\"taskid\":\"f7cb206407d040c9a5884a4dd954e8f9\",\"mobile\":\"13934855599\",\"ext\":\"43\",\"arrivedStatus \":\"0 \",\"arrivedTime \":\"2016 - 09 - 17 10: 07: 00 \"}]}";//可正常使用的数据
          ThirdTykjReportResult thirdTykjReportTesult = (ThirdTykjReportResult) JsonMapper.fromJsonString(str, ThirdTykjReportResult.class);


            if (thirdTykjReportTesult != null && thirdTykjReportTesult.getData() != null)
            {
                List<ThirdTykjReportSimple> listThirdTykjReportSimple = thirdTykjReportTesult.getData();
               return listThirdTykjReportSimple;
           }
            else
            {
           logger.info("返回状态报告为空");
             }
             return null;

     }

    /**
     * 获取状态报告
     *对象列表存入redis
     * @return
     */
    @RequestMapping(value = "getArrivedStatus")
    @ResponseBody
    public Map<String,Object> getArrivedStatus(HttpServletRequest request) {
        Map<String, Object> result = Maps.newHashMap();
        if (request != null) {
            List<ThirdTykjReportSimple>  thirdTykjReportSimples = generateStatusReport(request);
            if (thirdTykjReportSimples.size() != 0 && thirdTykjReportSimples !=null) {
                result.put("data",thirdTykjReportSimples);
                result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "获取状态报告成功"));

                pushIntoRedis(thirdTykjReportSimples);
             } else {
                result.put("data","1");
                result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "获取状态报告失败"));
;
            }
            return result;
        }
        return  null;
    }

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

    /**
     * 存入redis
     * @param thirdTykjReportSimples  要存入redis的数据
     */
    public void  pushIntoRedis(List<ThirdTykjReportSimple> thirdTykjReportSimples)
    {
        if(thirdTykjReportSimples!=null) {
            for (ThirdTykjReportSimple thirdTykjReportSimp: thirdTykjReportSimples) {
                FetchReportParaRedisTy rmr = new FetchReportParaRedisTy(thirdTykjReportSimp.getTaskid(), thirdTykjReportSimp.getMobile(), thirdTykjReportSimp.getArrivedStatus(),thirdTykjReportSimp.getArrivedTime());
                JedisUtils.pushObject(Global.FTCHREPORT_REDIS_KEY, rmr, 0);
            }
        }
    }
}
