package com.ty.modules.tunnel.send.container.entity.cmpp;
import com.ty.common.mapper.JsonMapper;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import java.nio.charset.CharacterCodingException;


/**
 * 
* <p>Title: </p>
* <p>Description:链路检查响应消息结构定义</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午03:04:32
 */
public class MsgActiveTestResp extends MsgHead {
	private static Logger logger= Logger.getLogger(MsgActiveTestResp.class);
	private byte reserved;
	
	public static MsgActiveTestResp toObject (MsgHead msgHead,IoBuffer in){
		MsgActiveTestResp msgActiveTestResp=new MsgActiveTestResp();
		msgActiveTestResp.setTotalLength(msgHead.getTotalLength());
		msgActiveTestResp.setCommandId(msgHead.getCommandId());
		msgActiveTestResp.setSequenceId(msgHead.getSequenceId());
		msgActiveTestResp.setReserved(in.get());
		logger.debug("smsBoss-MsgActiveTestResp.toObject:"+ JsonMapper.toJsonString(msgActiveTestResp));
		return msgActiveTestResp;
	}

	public IoBuffer toIoBuffer(){
		IoBuffer in= IoBuffer.allocate(200).setAutoExpand(true);
		in.putUnsignedInt(this.getTotalLength());
		in.putUnsignedInt(this.getCommandId());
		in.putUnsignedInt(this.getSequenceId());
		in.put(this.getReserved());
		return in;
	}
	
	public byte getReserved() {
		return reserved;
	}

	public void setReserved(byte reserved) {
		this.reserved = reserved;
	}
}
