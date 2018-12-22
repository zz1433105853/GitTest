package com.ty.modules.tunnel.send.disruptor;

import com.ty.modules.msg.entity.MsgRecord;

/**
 * Created by ljb on 2017/4/18 15:28.
 */
public interface CommonProducer {

    void onData(MsgRecord msgRecord);

}
