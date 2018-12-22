package com.ty.modules.tunnel.send.container.type;

import com.ty.modules.msg.entity.Tunnel;
import com.ty.modules.tunnel.send.container.entity.MessageSend;

import java.io.IOException;

/**
 * Created by 阿水 on 2017/4/11 10:15.
 * 短信发送统一接口
 */
public interface MessageContainer {

    /**
     * 发送短信，并返回发送结果
     * @return
     */
    boolean sendMsg(MessageSend messageSend);

    /**
     * 检测Container是否可用
     * @return
     */
    boolean checkContainerIsActive();

    /**
     * 获取该Container关联的通道名称或者ID
     * @return
     */
    String getTdName();

    /**
     * 获取该Container关联的通道类型
     * @return
     */
    String getTunnelType();

    /**
     * 获取该Container关联的通道对象
     * @return
     */
    Tunnel getTunnel();



}
