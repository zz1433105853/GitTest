package com.ty.modules.msg.web;

import com.google.common.collect.Maps;
import com.ty.common.JavaBeanUtil;
import com.ty.common.config.Global;
import com.ty.common.utils.JedisUtils;
import com.ty.common.web.BaseController;
import com.ty.modules.tunnel.send.container.entity.container.MapToClass;
import com.ty.modules.tunnel.send.container.entity.container.klws.Arrivedklws;
import com.ty.modules.tunnel.send.container.entity.container.klws.FetchReportParaRedisKlws;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping ("msgKlws")
public class MessageKlwsTunelController extends BaseController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    HttpServletRequest request;

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
     * 获取到的参数转化为一个对象列表类
     *
     * @param request
     * @return
     */
    @RequestMapping("generateStatusReport")
    @ResponseBody
    public List<Arrivedklws> generateStatusReport(HttpServletRequest request) {


        Map<String, Object> map = getParamters(request);

        //简单对象
        MapToClass testData = (MapToClass) JavaBeanUtil.convertMapToBean(MapToClass.class, map);//map转成一个类，key的值是类的属性值，可能是各种格式的字符串，例如：json 拼接的串
        //拼接串
        String str = testData.getData();
        List<Arrivedklws> simpleList = new ArrayList<>();
        if (str != null) {
            ArrayList<String> arrayList3 = getOutListIs(str);


            for (int i = 0; i < arrayList3.size(); i++) {

                ArrayList<String> arrayList = getInListIs(arrayList3.get(i));
                if (arrayList != null) {
                    Arrivedklws klwsSimple = new Arrivedklws();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for (int j = 0; j < arrayList.size(); j = j + 1) {
                        //  logger.info("快乐网视状态报告:" + arrayList.get(j));

                        klwsSimple.setMsgid(arrayList.get(0));
                        klwsSimple.setMobile(arrayList.get(1));
                        klwsSimple.setStat(arrayList.get(2));
                        klwsSimple.setArrivedTime(df.format(new Date()));

                    }
                    simpleList.add(klwsSimple);//构造对象
                }

            }
        }
        return simpleList;

    }




    /**
     * 获取状态报告
     *
     * @return
     */
    @RequestMapping(value = "getArrivedStatus")
    @ResponseBody
    public Map<String,Object> getArrivedStatus(HttpServletRequest request) {
        Map<String, Object> result = Maps.newHashMap();
        if (request != null) {
            // List<Arrivedklws> arrivedklwss = generateStatusReport(request);
            List<Arrivedklws> arrivedklwss = generateStatusReport(request);
            if (arrivedklwss.size() != 0) {
                // result.put("data", arrivedklwss);
                //  String aa="简单变量";
                result.put("data","0");

                pushIntoRedis(arrivedklwss);
                //logger.info("json  压入数据成功");
            } else {
                result.put("data","1");
                //  result.put("status", Status.buildStatus(ResponseCode.SUCCESS, "获取状态报告失败"));
                // logger.info("json  压入数据失败");
            }
            return result;
        }
        return  null;
    }

    /**
     * 存入redis
     * @param arrivedklwss  要存入redis的数据
     */
    public void  pushIntoRedis(List<Arrivedklws> arrivedklwss)
    {
        if(arrivedklwss!=null) {
            for (Arrivedklws arrivedklws : arrivedklwss) {
                FetchReportParaRedisKlws rmr = new FetchReportParaRedisKlws(arrivedklws.getMsgid(), arrivedklws.getMobile(), arrivedklws.getStat(),arrivedklws.getArrivedTime());
                JedisUtils.pushObject(Global.FTCHREPORTKLWS_REDIS_KEY, rmr, 0);
            }
        }
    }

    /*
     * 将一个字符串用逗号分隔并存入到List中
     */
    public static ArrayList<String> getInListIs(String inStr) {
        ArrayList<String> InArrayList1 = new ArrayList<String>();

        InArrayList1.addAll(Arrays.asList(inStr.split(",")));

        return InArrayList1;
    }

    /*
     * 将一个字符串用|分隔并存入到List中
     */
    public static ArrayList<String> getOutListIs(String outStr) {
        ArrayList<String> OutArrayList2 = new ArrayList<String>();
        if (outStr != null) {
            OutArrayList2.addAll(Arrays.asList(outStr.split("\\|")));
        }

        return OutArrayList2;
    }
}
