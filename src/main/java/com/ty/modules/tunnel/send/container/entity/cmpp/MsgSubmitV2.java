package com.ty.modules.tunnel.send.container.entity.cmpp;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import java.nio.charset.CharacterCodingException;


/**
 * 
* <p>Title: </p>
* <p>Description:SP在与ISMG建立应用层连接后向ISMG提交短信</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午03:07:01
 */
public class MsgSubmitV2 extends MsgHead {
	private static Logger logger= Logger.getLogger(MsgSubmitV2.class);

	private long msgId = 0;
	private byte pkTotal = 0x01;
	private byte pkNumber = 0x01;
	private byte registeredDelivery = 0x00;
	private byte msgLevel = 0x01;
	private String serviceId ="";
	private byte feeUserType =0x00;// 谁接收，计谁的费
	private String feeTerminalId ="";
	private byte feeTerminalType = 0x00;
	private byte tpPId = 0x00;
	private byte tpUdhi = 0x00;
	private byte msgFmt = 0x0f;
	private String msgSrc;
	// 01：对“计费用户号码”免费；
	// 02：对“计费用户号码”按条计信息费；
	// 03：对“计费用户号码”按包月收取信息费
	private String feeType = "01";// 默认为按条
	private String feeCode = "000000";
	private String valIdTime = "";// 暂不支持
	private String atTime = "";// 暂不支持
	// SP的服务代码或前缀为服务代码的长号码,
	// 网关将该号码完整的填到SMPP协议Submit_SM消息相应的source_addr字段，该号码最终在用户手机上显示为短消息的主叫号码。
	private String srcId;
	private byte destUsrTl = 0x01;// 不支持群发
	private String destTerminalId;// 接收手机号码
	private byte destTerminalType = 0x00;// 真实号码
	private byte msgLength;
	private byte[] msgContent;
	private String linkID = "";// 点播业务使用的LinkID，非点播类业务的MT流程不使用该字段
	private String reserve = "";// 点播业务使用的LinkID，非点播类业务的MT流程不使用该字段

	private String id;

	public IoBuffer toIoBuffer(){
		IoBuffer in= IoBuffer.allocate(200).setAutoExpand(true);
		try {
			in.putUnsignedInt(this.getTotalLength());
			in.putUnsignedInt(this.getCommandId());
			in.putUnsignedInt(this.getSequenceId());
			in.putLong(this.msgId);//Msg_Id 信息标识，由SP接入的短信网关本身产生，本处填空
			in.put(this.pkTotal);//Pk_total 相同Msg_Id的信息总条数
			in.put(this.pkNumber);//Pk_number 相同Msg_Id的信息序号，从1开始
			in.put(this.registeredDelivery);//Registered_Delivery 是否要求返回状态确认报告
			in.put(this.msgLevel);//Msg_level 信息级别
			in.putString(this.serviceId, 10, super.getEncoder());//Service_Id 业务标识，是数字、字母和符号的组合。
			in.put(this.getFeeUserType());//Fee_UserType 计费用户类型字段 0：对目的终端MSISDN计费；1：对源终端MSISDN计费；2：对SP计费;3：表示本字段无效，对谁计费参见Fee_terminal_Id字段。
			in.putString(this.feeTerminalId, 21,super.getEncoder());//Fee_terminal_Id 被计费用户的号码
			in.put(this.tpPId);//TP_pId
			in.put(this.tpUdhi);//TP_udhi
			in.put(this.msgFmt);//Msg_Fmt
		
			in.putString(this.msgSrc, 6, super.getEncoder());//Msg_src 信息内容来源(SP_Id)
			in.putString(this.feeType, 2, super.getEncoder());//FeeType 资费类别
			in.putString(this.feeCode, 6, super.getEncoder());//FeeCode
			in.putString(this.valIdTime, 17, super.getEncoder());//存活有效期
			in.putString(this.atTime, 17, super.getEncoder());//定时发送时间
			in.putString(this.srcId, 21, super.getEncoder());//Src_Id spCode
			in.put(this.destUsrTl);//DestUsr_tl
			in.putString(this.destTerminalId, 21, super.getEncoder());//Dest_terminal_Id
			in.put(this.msgLength);//Msg_Length
			in.put(this.msgContent, 0, this.msgLength & 0xff);//信息内容
			in.putString(this.reserve, 8, super.getEncoder());

		} catch (CharacterCodingException e) {
			logger.error("封装MsgSubmit二进制数组失败。"+e.getMessage());
		}
		return in;
	}

	public static MsgSubmitV2 toObject(MsgHead msgHead, IoBuffer in){
		MsgSubmitV2 msgSubmit=new MsgSubmitV2();
		try {
			msgSubmit.setTotalLength(msgHead.getTotalLength());
			msgSubmit.setCommandId(msgHead.getCommandId());
			msgSubmit.setSequenceId(msgHead.getSequenceId());
			msgSubmit.setMsgId(in.getLong());
			msgSubmit.setPkTotal(in.get());
			msgSubmit.setPkNumber(in.get());
			msgSubmit.setRegisteredDelivery(in.get());
			msgSubmit.setMsgLevel(in.get());
			msgSubmit.setServiceId(in.getString(10, getDecoder()));
			msgSubmit.setFeeUserType(in.get());
			msgSubmit.setFeeTerminalId(in.getString(32, getDecoder()));
			msgSubmit.setFeeTerminalType(in.get());
			msgSubmit.setTpPId(in.get());
			msgSubmit.setTpUdhi(in.get());
			msgSubmit.setMsgFmt(in.get());		
			msgSubmit.setMsgSrc(in.getString(6, getDecoder()));
			msgSubmit.setFeeType(in.getString(2, getDecoder()));
			msgSubmit.setFeeCode(in.getString(6, getDecoder()));
			msgSubmit.setValIdTime(in.getString(17, getDecoder()));
			msgSubmit.setAtTime(in.getString(17, getDecoder()));
			msgSubmit.setSrcId(in.getString(21, getDecoder()));
			msgSubmit.setDestUsrTl(in.get());
			msgSubmit.setDestTerminalId(in.getString(32, getDecoder()));
			msgSubmit.setDestTerminalType(in.get());
			msgSubmit.setMsgLength(in.get());
			byte[] msg_ContentByte=new byte[msgSubmit.getMsgLength()];
			in.get(msg_ContentByte, 0, msgSubmit.getMsgLength());
			msgSubmit.setMsgContent(msg_ContentByte);
			msgSubmit.setLinkID(in.getString(20, getDecoder()));
		} catch (CharacterCodingException e) {
			logger.error("MsgSubmit二进制数组转MsgSubmit失败。"+e.getMessage());
		}
		return msgSubmit;
	}
	
	

	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
	public byte getPkTotal() {
		return pkTotal;
	}
	public void setPkTotal(byte pkTotal) {
		this.pkTotal = pkTotal;
	}
	public byte getPkNumber() {
		return pkNumber;
	}
	public void setPkNumber(byte pkNumber) {
		this.pkNumber = pkNumber;
	}
	public byte getRegisteredDelivery() {
		return registeredDelivery;
	}
	public void setRegisteredDelivery(byte registeredDelivery) {
		this.registeredDelivery = registeredDelivery;
	}
	public byte getMsgLevel() {
		return msgLevel;
	}
	public void setMsgLevel(byte msgLevel) {
		this.msgLevel = msgLevel;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public byte getFeeUserType() {
		return feeUserType;
	}
	public void setFeeUserType(byte feeUserType) {
		this.feeUserType = feeUserType;
	}
	public String getFeeTerminalId() {
		return feeTerminalId;
	}
	public void setFeeTerminalId(String feeTerminalId) {
		this.feeTerminalId = feeTerminalId;
	}
	public byte getFeeTerminalType() {
		return feeTerminalType;
	}
	public void setFeeTerminalType(byte feeTerminalType) {
		this.feeTerminalType = feeTerminalType;
	}
	public byte getTpPId() {
		return tpPId;
	}
	public void setTpPId(byte tpPId) {
		this.tpPId = tpPId;
	}
	public byte getTpUdhi() {
		return tpUdhi;
	}
	public void setTpUdhi(byte tpUdhi) {
		this.tpUdhi = tpUdhi;
	}
	public byte getMsgFmt() {
		return msgFmt;
	}
	public void setMsgFmt(byte msgFmt) {
		this.msgFmt = msgFmt;
	}
	public String getMsgSrc() {
		return msgSrc;
	}
	public void setMsgSrc(String msgSrc) {
		this.msgSrc = msgSrc;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public String getFeeCode() {
		return feeCode;
	}
	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}
	public String getValIdTime() {
		return valIdTime;
	}
	public void setValIdTime(String valIdTime) {
		this.valIdTime = valIdTime;
	}
	public String getAtTime() {
		return atTime;
	}
	public void setAtTime(String atTime) {
		this.atTime = atTime;
	}
	public String getSrcId() {
		return srcId;
	}
	public void setSrcId(String srcId) {
		this.srcId = srcId;
	}
	public byte getDestUsrTl() {
		return destUsrTl;
	}
	public void setDestUsrTl(byte destUsrTl) {
		this.destUsrTl = destUsrTl;
	}

	public String getDestTerminalId() {
		return destTerminalId;
	}

	public void setDestTerminalId(String destTerminalId) {
		this.destTerminalId = destTerminalId;
	}

	public byte getDestTerminalType() {
		return destTerminalType;
	}
	public void setDestTerminalType(byte destTerminalType) {
		this.destTerminalType = destTerminalType;
	}
	public byte getMsgLength() {
		return msgLength;
	}
	public void setMsgLength(byte msgLength) {
		this.msgLength = msgLength;
	}
	public byte[] getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(byte[] msgContent) {
		this.msgContent = msgContent;
	}
	public String getLinkID() {
		return linkID;
	}
	public void setLinkID(String linkID) {
		this.linkID = linkID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}
}
