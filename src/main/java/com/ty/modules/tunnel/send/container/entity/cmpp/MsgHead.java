package com.ty.modules.tunnel.send.container.entity.cmpp;

import com.ty.common.mapper.JsonMapper;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * 
* <p>Title: </p>
* <p>Description:所有请求的消息头</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午02:10:56
 */
public class MsgHead {
	private static Logger logger= Logger.getLogger(MsgHead.class);
	private static CharsetEncoder encoder=Charset.forName("UTF-8").newEncoder();
	private static CharsetDecoder decoder=Charset.forName("UTF-8").newDecoder();
	private long totalLength;	//消息总长度
	private long commandId;		//命令类型
	private long sequenceId;	//消息流水号,顺序累加,步长为1,循环使用（一对请求和应答消息的流水号必须相同）
	
	public IoBuffer toIoBuffer(){
		IoBuffer in= IoBuffer.allocate(200).setAutoExpand(true);
		in.putUnsignedInt(this.getTotalLength());
		in.putUnsignedInt(this.getCommandId());
		in.putUnsignedInt(this.getSequenceId());
		return in;
	}
	
	public static MsgHead toObject(IoBuffer in){
		MsgHead msgHead=new MsgHead();
		msgHead.setTotalLength(in.getUnsignedInt());
		msgHead.setCommandId(in.getUnsignedInt());
		msgHead.setSequenceId(in.getUnsignedInt());
		logger.debug("smsBoss-MsgHead.toObject:"+ JsonMapper.toJsonString(msgHead));
		return msgHead;
	}
	
	public static CharsetEncoder getEncoder(){
		return encoder;
	}
	
	public static CharsetDecoder getDecoder(){
		return decoder;
	}
	
	public MsgHead(){
		super();
	}
	public long getTotalLength() {
		return totalLength;
	}
	public void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
	}
	public long getCommandId() {
		return commandId;
	}
	public void setCommandId(long commandId) {
		this.commandId = commandId;
	}
	public long getSequenceId() {
		return sequenceId;
	}
	public void setSequenceId(long sequenceId) {
		this.sequenceId = sequenceId;
	}
}
