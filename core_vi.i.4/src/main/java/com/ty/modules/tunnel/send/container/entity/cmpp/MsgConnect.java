package com.ty.modules.tunnel.send.container.entity.cmpp;

import com.ty.common.mapper.JsonMapper;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import java.nio.charset.CharacterCodingException;

/**
* <p>Title: </p>
* <p>Description:SP请求连接到ISMG</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午02:21:19
 */
public class MsgConnect extends MsgHead{
	private static Logger logger= Logger.getLogger(MsgConnect.class);
	private String sourceAddr;			//源地址，此处为SP_Id，即SP的企业代码。
	private byte[] authenticatorSource;	//用于鉴别源地址。其值通过单向MD5 hash计算得出，表示如下：AuthenticatorSource = MD5（Source_Addr+9 字节的0 +shared secret+timestamp） Shared secret 由中国移动与源地址实体事先商定，timestamp格式为：MMDDHHMMSS，即月日时分秒，10位。
	private byte version=0x30;				//双方协商的版本号(高位4bit表示主版本号,低位4bit表示次版本号)，对于3.0的版本，高4bit为3，低4位为0
	private long timestamp;				//时间戳的明文,由客户端产生,格式为MMDDHHMMSS，即月日时分秒，10位数字的整型，右对齐 。
	

	public IoBuffer toIoBuffer(){
		IoBuffer in= IoBuffer.allocate(200).setAutoExpand(true);
		try {
			in.putUnsignedInt(this.getTotalLength());
			in.putUnsignedInt(this.getCommandId());
			in.putUnsignedInt(this.getSequenceId());
			in.putString(this.getSourceAddr(), 6, super.getEncoder());
			in.put(this.getAuthenticatorSource());
			in.put(this.getVersion());
			in.putUnsignedInt(this.getTimestamp());
		} catch (CharacterCodingException e) {
			logger.error("封装MsgConnect二进制数组失败。"+e.getMessage());
		}
		return in;
	}

	public static MsgConnect toObject(MsgHead msgHead,IoBuffer in){
		MsgConnect msgConnect=new MsgConnect();
		try {
			msgConnect.setTotalLength(msgHead.getTotalLength());
			msgConnect.setCommandId(msgHead.getCommandId());
			msgConnect.setSequenceId(msgHead.getSequenceId());					
			msgConnect.setSourceAddr(in.getString(6, getDecoder()));
			byte[] byteArray=new byte[16]; 
			in.get(byteArray, 0, 16);			
			msgConnect.setAuthenticatorSource(byteArray);
			msgConnect.setVersion(in.get());
			msgConnect.setTimestamp(in.getUnsignedInt());
			logger.debug("smsBoss-MsgConnect.toObject:"+ JsonMapper.toJsonString(msgConnect));
		} catch (CharacterCodingException e) {
			logger.error("MsgConnect二进制数组转MsgConnect失败。"+e.getMessage());
		}
		return msgConnect;
	}

	public String getSourceAddr() {
		return sourceAddr;
	}
	public void setSourceAddr(String sourceAddr) {
		this.sourceAddr = sourceAddr;
	}
	public byte[] getAuthenticatorSource() {
		return authenticatorSource;
	}
	public void setAuthenticatorSource(byte[] authenticatorSource) {
		this.authenticatorSource = authenticatorSource;
	}
	public byte getVersion() {
		return version;
	}
	public void setVersion(byte version) {
		this.version = version;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
