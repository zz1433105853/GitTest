package com.ty.modules.core.common.disruptor.message;

/**
 * disruptor-code
 * Created by Ljb on 2016/11/21.
 */
public class ProductCode {
    public final static String PRE_MESSAGE = "preMessage";//提交短信
    public final static String CLIENT_PRE_MESSAGE = "clientPreMessage";//客户端提交短信
    public final static String SEND_MESSAGE = "sendMessage";//短信发送
    public final static String SEND_SPECIAL_MESSAGE = "sendSpecialMessage";//个性短信发送
    public final static String CLIENT_SEND_MESSAGE = "clientSendMessage";//客户端短信发送
    public final static String POST_SUBMIT_RESPONSE = "postSubmitResponse";//短信提交响应
    public final static String POST_STATUS_RESPONSE = "postStatusResponse";//短信状态报告
}
