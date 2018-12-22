package com.ty.modules.tunnel.send.container.cx;

import com.ty.modules.msg.entity.CxTunnel;
import com.ty.modules.msg.entity.MsgCxRecord;
import com.ty.modules.msg.entity.Tunnel;
import com.ty.modules.tunnel.send.container.entity.MessageSend;

/**
 * Created by ljb on 2017/4/11 10:15.
 * 彩信发送统一接口
 */
public interface MessageCxContainer {

    /**
     * 发送彩信，并返回发送结果
     * @return
     */
    boolean sendCx(MsgCxRecord msgCxRecord);

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
    CxTunnel getTunnel();



}
