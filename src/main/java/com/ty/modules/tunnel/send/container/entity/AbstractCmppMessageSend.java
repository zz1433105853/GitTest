package com.ty.modules.tunnel.send.container.entity;

import com.ty.common.utils.DateUtils;
import com.ty.modules.msg.entity.MsgRecord;

import java.util.Date;

/**
 * Created by ljb on 2017/4/12 15:13.
 */
public abstract class AbstractCmppMessageSend extends AbstractMessageSend{

    private static final long serialVersionUID = 1609118310094315704L;

    protected Date intoWindowMapTime;//进入直连通道WindowMap的时间

    public AbstractCmppMessageSend(MsgRecord msgRecord) {
        super(msgRecord);
    }

    /**
     * 打印该消息在WindowMap中存在的时间长短
     * @return
     */
    public String printMessageSendLiveInWindowMapTime() {
        StringBuilder result = new StringBuilder();
        if(intoWindowMapTime!=null) {
            long ml = System.currentTimeMillis()-intoWindowMapTime.getTime();
            result.append("进入时间："+ DateUtils.formatDateTime(intoWindowMapTime)+" 已经待了："+ DateUtils.formatDateTime(ml)+ "\r\n");
        }
        return result.toString();
    }

    public Date getIntoWindowMapTime() {
        return intoWindowMapTime;
    }

    public void setIntoWindowMapTime(Date intoWindowMapTime) {
        this.intoWindowMapTime = intoWindowMapTime;
    }

}
