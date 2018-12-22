package com.ty.modules.tunnel.send.container.type;


import com.ty.modules.tunnel.send.container.entity.AbstractCmppMessageSend;
import com.ty.modules.tunnel.send.container.entity.MessageSend;
import com.ty.modules.tunnel.send.container.entity.cmpp.MsgConfig;
import org.apache.mina.core.session.IoSession;

import java.util.Map;

/**
 * Created by ljb on 2017/4/11 10:17.
 * 直连通道Container
 */
public interface StraightMessageContainer extends MessageContainer{

    /**
     * 初始化通道信息
     */
    void init();

    /**
     * 获取下一序列号
     * @return
     */
    int getSequence();

    /**
     * 发送链路检测
     */
    void activityTestISMG();

    /**
     * 与通道发送连接消息
     * @param ioSession
     */
    void connectISMG(IoSession ioSession);

    /**
     * 取消与通道的连接
     */
    void cancelISMG();

    /**
     * 查询消息
     * @param time
     * @param queryType
     * @param queryCode
     */
    void queryISMG(String time, byte queryType, String queryCode);

    /**
     * 发送状态报告响应
     * @param msg_Id
     * @param seqId
     * @param result
     */
    void sendMsgDeliverResp(long msg_Id, long seqId, int result);

    void sendActiveTestResp(long seqId);
    /**
     * 启动链路检测线程
     */
    void startActiveTestThread();

    /**
     * 获取发送消息的WindowMap对象
     * @return
     */
    Map<String, AbstractCmppMessageSend> getWindowMap();

    /**
     * 发送短短信
     * @param msg
     * @param destTermId
     * @param srcId
     * @param messageSend
     * @return
     */
    boolean sendShortMsg(String msg, String destTermId, String srcId, MessageSend messageSend);

    /**
     * 发送长短信
     * @param msg
     * @param destTermId
     * @param srcId
     * @param messageSend
     * @return
     */
    boolean sendLongMsg(String msg, String destTermId, String srcId, MessageSend messageSend);

    /**
     * 发送彩信消息
     * @param messageSend
     * @return
     */
    boolean sendWapPushMsg(MessageSend messageSend);

    /**
     * 发送短彩信消息
     * @param url
     * @param desc
     * @param destTermId
     * @return
     */
    boolean sendShortWapPushMsg(String url, String desc, String destTermId);

    /**
     * 发送长彩信消息
     * @param url
     * @param desc
     * @param destTermId
     * @return
     */
    boolean sendLongWapPushMsg(String url, String desc, String destTermId);

    /**
     * 重连通道
     * @param isAuto
     */
    void reConnect(boolean isAuto);

    /**
     * 检测通道是否为最佳发送时间
     * @return
     */
    boolean checkSendInterval();

    /**
     * 检测当前WindowMap的大小
     * @return
     */
    boolean checkWindowMapSize();

    /**
     * 获取当前Container的通道配置
     * @return
     */
    MsgConfig getMsgConfig();

}
