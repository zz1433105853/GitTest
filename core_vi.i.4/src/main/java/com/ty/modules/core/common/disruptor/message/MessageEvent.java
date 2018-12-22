package com.ty.modules.core.common.disruptor.message;


import com.ty.modules.msg.entity.Message;
import com.ty.modules.msg.entity.MessageSend;

/**
 * Created by Ljb on 2016/5/26.
 */
public class MessageEvent {

    private MessageCore messageCore;
    private Message message;
    private MessageSend messageSend;

    public MessageCore getMessageCore() {
        return messageCore;
    }

    public void setMessageCore(MessageCore messageCore) {
        this.messageCore = messageCore;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MessageSend getMessageSend() {
        return messageSend;
    }

    public void setMessageSend(MessageSend messageSend) {
        this.messageSend = messageSend;
    }
}
