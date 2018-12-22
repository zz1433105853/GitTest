package com.ty.modules.tunnel.send.container.entity;

import com.ty.modules.msg.entity.MsgResponse;

/**
 * Created by ljb on 2017/4/12 15:34.
 */
public interface MessageSend {

    /**
     * 由子类继承实现自己的返回响应实体
     * @return
     */
    MsgResponse getMsgResponse();

    /**
     * 获取本次发送具体内容
     * @return
     */
    String getContent();

    /**
     * 获取本次发送具体手机号
     * @return
     */
    String getMobile();

    /**
     * 获取实际CMPP调用发送消息内容
     * @return
     */
    String getMsgContent();

    /**
     * 获取实际CMPP发送电话
     * @return
     */
    String getMsgMobile();

    /**
     * 获取消息MSgID
     * @return
     */
    String getMsgId();

    /**
     * 获取序列号
     * @return
     */
    String getSeqId();

    /**
     * 获取系统定义状态吗
     * @return
     */
    String getResult();

    /**
     * 获取通道返回状态信息
     * @return
     */
    String getResultMessage();

    /**
     * 获取关联发送记录Id
     * @return
     */
    String getRecordId();
}
