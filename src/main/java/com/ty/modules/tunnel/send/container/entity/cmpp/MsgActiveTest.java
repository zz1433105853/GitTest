package com.ty.modules.tunnel.send.container.entity.cmpp;
import com.ty.common.mapper.JsonMapper;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;


/**
 * 
* <p>Title: </p>
* <p>Description:链路检查结构定义</p>
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午03:04:32
 */
public class MsgActiveTest extends MsgHead {
	private static Logger logger= Logger.getLogger(MsgActiveTest.class);
	private byte reserved;
	
	public static MsgActiveTest toObject (MsgHead msgHead, IoBuffer in){
		MsgActiveTest msgActiveTest=new MsgActiveTest();
		msgActiveTest.setTotalLength(msgHead.getTotalLength());
		msgActiveTest.setCommandId(msgHead.getCommandId());
		msgActiveTest.setSequenceId(msgHead.getSequenceId());
		logger.debug("smsBoss-MsgActiveTest.toObject:"+ JsonMapper.toJsonString(msgActiveTest));
		return msgActiveTest;
	}
	
	public byte getReserved() {
		return reserved;
	}

	public void setReserved(byte reserved) {
		this.reserved = reserved;
	}
}
