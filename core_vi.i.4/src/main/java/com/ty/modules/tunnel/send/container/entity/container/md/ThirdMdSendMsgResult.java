package com.ty.modules.tunnel.send.container.entity.container.md;

import com.google.common.collect.Maps;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.Map;

/**
 * Created by Ysw on 2016/7/4.
 */
@XmlRootElement(name = "string", namespace = "http://tempuri.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThirdMdSendMsgResult {

    @XmlValue
    private String rrid;

    public ThirdMdSendMsgResult() {
        this.rrid = "-1";
    }

    public ThirdMdSendMsgResult(String rrid) {
        this.rrid = rrid;
    }

    public String getRrid() {
        return rrid;
    }

    public void setRrid(String rrid) {
        this.rrid = rrid;
    }

    public boolean isSuccess() {
        if(errCode.contains(rrid)) {
            return false;
        }else {
            return true;
        }
    }

    @Override
    public String toString() {
        if(isSuccess()) {
            return "发送成功，消息批次ID："+rrid;
        }else {
            return errMap.get(rrid);
        }
    }

    private static String errCode = "-1,-2,-4,-5,-6,-7,-8,-9,-10,-11,-12,-14,-17,-19,-20,-22,-23,-601,-602,-603,-604,-605,-606,-607,-608,-609,-610,-611,-623,-624,-625,-626";
    private static String errReason = "未知原因失败,帐号/密码不正确,余额不足支持本次发送,数据格式错误,参数有误,权限受限,流量控制错误,扩展码权限错误,内容长度长,内部数据库错误,序列号状态错误,服务器写文件失败,没有权限,禁止同时使用多个接口地址,相同手机号，相同内容重复提交,Ip鉴权失败,缓存无此序列号信息,序列号为空，参数错误,序列号格式错误，参数错误,密码为空，参数错误,手机号码为空，参数错误,内容为空，参数错误,ext长度大于9，参数错误,参数错误 扩展码非数字 ,参数错误 定时时间非日期格式,rrid长度大于1,参数错误 ,参数错误 rrid非数字,参数错误 内容编码不符合规范,手机个数与内容个数不匹配,扩展个数与手机个数数,定时时间个数与手机个数数不匹配,rrid个数与手机个数数不匹配";
    private static Map<String, String> errMap = Maps.newHashMap();
    static {
        String[] errCodeArr = errCode.split(",");
        String[] errReasonArr = errReason.split(",");
        for(int i=0;i<errCodeArr.length;i++) {
            errMap.put(errCodeArr[i], errReasonArr[i]);
        }
    }

}
