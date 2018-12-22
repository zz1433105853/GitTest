package com.ty.modules.tunnel.send.container.type;

/**
 * Created by 阿水 on 2017/4/11 10:18.
 * 第三方非直连通道Container
 */
public interface SdkMessageContainer extends MessageContainer{

    /**
     * 获取通道余额
     * @return
     */
    long getBalance();

    /**
     * 获取状态报告
     * @return
     */
    void triggerMsgReportFetch();

    /**
     * 获取上行报告
     * @return
     */
    void triggerMsgReplyFetch();

}
