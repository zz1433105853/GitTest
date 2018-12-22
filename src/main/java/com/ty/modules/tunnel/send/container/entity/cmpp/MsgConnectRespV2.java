package com.ty.modules.tunnel.send.container.entity.cmpp;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

/**
* <p>Title: </p>
* <p>Description:ISMG以CMPP_CONNECT_RESP消息响应SP的链接请求</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午02:45:59
 */
public class MsgConnectRespV2 extends MsgHead {
	private static Logger logger= Logger.getLogger(MsgConnectRespV2.class);
	private long status;					//响应状态状态 0：正确 1：消息结构错 2：非法源地址 3：认证错 4：版本太高 5~ ：其他错误
	private String statusStr;			//响应状态状态 0：正确 1：消息结构错 2：非法源地址 3：认证错 4：版本太高 5~ ：其他错误
	private byte[] authenticatorISMG;	//ISMG认证码，用于鉴别ISMG。 其值通过单向MD5 hash计算得出，表示如下： AuthenticatorISMG =MD5（Status+AuthenticatorSource+shared secret），Shared secret 由中国移动与源地址实体事先商定，AuthenticatorSource为源地址实体发送给ISMG的对应消息CMPP_Connect中的值。 认证出错时，此项为空。
	private byte version=0x20;				//服务器支持的最高版本号，对于3.0的版本，高4bit为3，低4位为0
	
	
	public static MsgConnectRespV2 toObject (MsgHead msgHead, IoBuffer in){
 		MsgConnectRespV2 msgConnectResp=new MsgConnectRespV2();
		msgConnectResp.setTotalLength(msgHead.getTotalLength());
		msgConnectResp.setCommandId(msgHead.getCommandId());
		msgConnectResp.setSequenceId(msgHead.getSequenceId());
		msgConnectResp.setStatus(in.get());
		byte[] byteArray=new byte[16];
		in.get(byteArray, 0, 16);
		msgConnectResp.setAuthenticatorISMG(byteArray);
		msgConnectResp.setVersion(in.get());
		return msgConnectResp;
	}
	
	
	
	public long getStatus() {
		return status;
	}
	public void setStatus(long status) {
		this.status = status;
		switch((int)status){
			case 0 : statusStr="正确";break;
			case 1 : statusStr="消息结构错";break;
			case 2 : statusStr="非法源地址";break;
			case 3 : statusStr="认证错";break;
			case 4 : statusStr="版本太高";break;
			case 5 : statusStr="其他错误";break;
			default:statusStr=status+":未知";break;
		}
	}
	public byte[] getAuthenticatorISMG() {
		return authenticatorISMG;
	}
	public void setAuthenticatorISMG(byte[] authenticatorISMG) {
		this.authenticatorISMG = authenticatorISMG;
	}
	public byte getVersion() {
		return version;
	}
	public void setVersion(byte version) {
		this.version = version;
	}
	public String getStatusStr() {
		return statusStr;
	}
}
