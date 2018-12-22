package com.ty.modules.tunnel.send.container.entity.cmpp;

import com.ty.common.mapper.JsonMapper;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;

/**
 * 
* <p>Title: </p>
* <p>Description:CMPP_DELIVER操作的目的是ISMG把从短信中心或其它ISMG转发来的短信送交SP，SP以CMPP_DELIVER_RESP消息回应</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午02:54:18
 */
public class MsgDeliver extends MsgHead implements MsgDeliverInterface{
	private static Logger logger= Logger.getLogger(MsgDeliver.class);
	private long msg_Id;//信息标识
	private String dest_Id;//21 目的号码 String 
	private String service_Id;//10 业务标识  String 
	private byte tP_pid = 0;//GSM协议类型
	private byte tP_udhi = 0;//GSM协议类型
	private byte msg_Fmt = 15;//信息格式
	private String src_terminal_Id;//源终端MSISDN号码
	private byte src_terminal_type = 0;//源终端号码类型，0：真实号码；1：伪码
	private byte registered_Delivery = 0;//是否为状态报告 0：非状态报告1：状态报告
	private int msg_Length;//消息长度
	private String msg_Content;//消息内容
	private String linkID;//点播业务使用的LinkID
	
	private long msg_Id_report;//信息标识。
	private String stat;//发送短信的应答结果
	private String submit_time;
	private String done_time;
	private String dest_terminal_Id;//目的终端MSISDN号码
	private long sMSC_sequence;//取自SMSC发送状态报告的消息体中的消息标识
	private int result;//解析结果
	private String reserved;

	public static MsgDeliver toObject(MsgHead msgHead,IoBuffer in){
		MsgDeliver msgDeliver=new MsgDeliver();
		try {

			String fmtStr="gbk";
			/*Head Info*/
			msgDeliver.setTotalLength(msgHead.getTotalLength());
			msgDeliver.setCommandId(msgHead.getCommandId());
			msgDeliver.setSequenceId(msgHead.getSequenceId());

			/*Body Info*/

			/*Msg_id*/
			msgDeliver.setMsg_Id(in.getLong());

			/*Dest_id*/
			byte[] destIdByte=new byte[21];
			in.get(destIdByte,0,21);
			msgDeliver.setDest_Id(new String(destIdByte));//21 目的号码 String 

			/*Service_Id*/
			byte[] service_IdByte=new byte[10];
			in.get(service_IdByte,0,10);
			msgDeliver.setService_Id(new String(service_IdByte));//10 业务标识  String

			/*TP_pid : TP_udhi : Msg_fmt*/
			msgDeliver.setTP_pid(in.get());
			msgDeliver.setTP_udhi(in.get());
			msgDeliver.setMsg_Fmt(in.get());
			
			fmtStr=msgDeliver.getMsg_Fmt()==8?"UTF-16BE":"gb2312";
//			fmtStr= "UTF-8";
			logger.info("用户上行内容编码："+fmtStr);
			/*Src_terminal_Id*/
			int byteLength = 32;//3.0:32
			byte[] src_terminal_IdByte=new byte[byteLength];
			in.get(src_terminal_IdByte, 0, byteLength);
			msgDeliver.setSrc_terminal_Id(new String(src_terminal_IdByte));//源终端MSISDN号码

			msgDeliver.setSrc_terminal_type(in.get());//源终端号码类型，0：真实号码；1：伪码  3.0才有

			/*Registered_Delivery*/
			msgDeliver.setRegistered_Delivery(in.get());//是否为状态报告 0：非z状态报告1：状态报告

			/*Msg_Length*/
			msgDeliver.setMsg_Length(in.getUnsigned());//消息长度

			if(msgDeliver.getRegistered_Delivery()==1){//如果为状态报告
				int destTerminalIdLength = 32;//3.0为32
				if(msgDeliver.getMsg_Length()==8+7+10+10+destTerminalIdLength+4){

					msgDeliver.setMsg_Id_report(in.getLong());
					
					byte[] statByte=new byte[7];
					in.get(statByte, 0, 7);
					msgDeliver.setStat(new String(statByte,fmtStr));
					
					byte[] submit_timeByte=new byte[10];
					in.get(submit_timeByte, 0, 10);
					msgDeliver.setSubmit_time(new String(submit_timeByte,fmtStr));
					
					byte[] done_timeByte=new byte[10];
					in.get(done_timeByte, 0, 10);
					msgDeliver.setDone_time(new String(done_timeByte,fmtStr));
					
					byte[] dest_terminal_IdByte=new byte[destTerminalIdLength];
					in.get(dest_terminal_IdByte, 0, destTerminalIdLength);
					msgDeliver.setDest_terminal_Id(new String(dest_terminal_IdByte,fmtStr));
					
					msgDeliver.setSMSC_sequence(in.getUnsignedInt());
					msgDeliver.setResult(0);//正确
				}else{
					msgDeliver.setResult(1);//消息结构错
				}
			}else{//如果为非状态报告
				byte[] msg_ContentByte=new byte[msgDeliver.getMsg_Length()];
				in.get(msg_ContentByte, 0, msgDeliver.getMsg_Length());
				msgDeliver.setMsg_Content(new String(msg_ContentByte,fmtStr));//消息内容
			}
			byte[] linkIDByte=new byte[20];
			in.get(linkIDByte, 0, 20);
			msgDeliver.setLinkID(new String(linkIDByte));

		} catch (IOException e){
			msgDeliver.setResult(8);//消息结构错
			logger.debug("短信网关CMPP_DELIVER,解析数据包出错");
		}
		logger.info("smsBoss-MsgDeliver.toObject:"+ JsonMapper.toJsonString(msgDeliver));
		return msgDeliver;
	}
	
	public long getMsg_Id() {
		return msg_Id;
	}

	public void setMsg_Id(long msg_Id) {
		this.msg_Id = msg_Id;
	}

	public String getDest_Id() {
		return dest_Id;
	}

	public void setDest_Id(String dest_Id) {
		this.dest_Id = dest_Id;
	}

	public String getService_Id() {
		return service_Id;
	}

	public void setService_Id(String service_Id) {
		this.service_Id = service_Id;
	}

	public byte getTP_pid() {
		return tP_pid;
	}

	public void setTP_pid(byte tp_pid) {
		tP_pid = tp_pid;
	}

	public byte getTP_udhi() {
		return tP_udhi;
	}

	public void setTP_udhi(byte tp_udhi) {
		tP_udhi = tp_udhi;
	}

	public byte getMsg_Fmt() {
		return msg_Fmt;
	}

	public void setMsg_Fmt(byte msg_Fmt) {
		this.msg_Fmt = msg_Fmt;
	}

	public String getSrc_terminal_Id() {
		return src_terminal_Id;
	}

	public void setSrc_terminal_Id(String src_terminal_Id) {
		this.src_terminal_Id = src_terminal_Id;
	}

	public byte getSrc_terminal_type() {
		return src_terminal_type;
	}

	public void setSrc_terminal_type(byte src_terminal_type) {
		this.src_terminal_type = src_terminal_type;
	}

	public byte getRegistered_Delivery() {
		return registered_Delivery;
	}

	public void setRegistered_Delivery(byte registered_Delivery) {
		this.registered_Delivery = registered_Delivery;
	}

	public int getMsg_Length() {
		return msg_Length;
	}

	public void setMsg_Length(int msg_Length) {
		this.msg_Length = msg_Length;
	}

	public String getMsg_Content() {
		return msg_Content;
	}

	public void setMsg_Content(String msg_Content) {
		this.msg_Content = msg_Content;
	}

	public String getLinkID() {
		return linkID;
	}

	public void setLinkID(String linkID) {
		this.linkID = linkID;
	}

	public long getMsg_Id_report() {
		return msg_Id_report;
	}

	public void setMsg_Id_report(long msgIdReport) {
		msg_Id_report = msgIdReport;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getSubmit_time() {
		return submit_time;
	}

	public void setSubmit_time(String submit_time) {
		this.submit_time = submit_time;
	}

	public String getDone_time() {
		return done_time;
	}

	public void setDone_time(String done_time) {
		this.done_time = done_time;
	}

	public String getDest_terminal_Id() {
		return dest_terminal_Id;
	}

	public void setDest_terminal_Id(String dest_terminal_Id) {
		this.dest_terminal_Id = dest_terminal_Id;
	}

	public long getSMSC_sequence() {
		return sMSC_sequence;
	}

	public void setSMSC_sequence(long smsc_sequence) {
		sMSC_sequence = smsc_sequence;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getReserved() {
		return reserved;
	}

	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
}
