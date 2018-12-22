package com.ty.modules.core.common.disruptor.message.entity;

import com.ty.modules.msg.entity.Customer;
import com.ty.modules.msg.entity.MessageSubmitLog;
import com.ty.modules.msg.entity.UserSubmit;

/**
 * 提交短信
 * Created by Ljb on 2016/11/21.
 */
public class PreMessage {
    private Customer customer;
    private UserSubmit userSubmit;
    private MessageSubmitLog messageSubmitLog;

    public PreMessage() {
    }

    public PreMessage(Customer customer, UserSubmit userSubmit,MessageSubmitLog messageSubmitLog) {
        this.customer = customer;
        this.userSubmit = userSubmit;
        this.messageSubmitLog = messageSubmitLog;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public UserSubmit getUserSubmit() {
        return userSubmit;
    }

    public void setUserSubmit(UserSubmit userSubmit) {
        this.userSubmit = userSubmit;
    }

    public MessageSubmitLog getMessageSubmitLog() {
        return messageSubmitLog;
    }

    public void setMessageSubmitLog(MessageSubmitLog messageSubmitLog) {
        this.messageSubmitLog = messageSubmitLog;
    }
}
