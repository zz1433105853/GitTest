package com.ty.modules.tunnel.send.container.entity.cmpp;

import com.ty.common.mapper.JsonMapper;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import java.nio.charset.CharacterCodingException;


/**
 * 
* <p>Title: </p>
* <p>Description:SP向ISMG查询发送短信状态</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午04:58:44
 */
public class MsgQuery extends MsgHead {
	private static Logger logger= Logger.getLogger(MsgQuery.class);
	private String time = "";
	private byte query_Type = 0x00;
	private String query_Code = "";
	private String reserve = "";
	
	public IoBuffer toIoBuffer(){
		IoBuffer in= IoBuffer.allocate(200).setAutoExpand(true);
		try {		
			in.putUnsignedInt(this.getTotalLength());
			in.putUnsignedInt(this.getCommandId());
			in.putUnsignedInt(this.getSequenceId());
			in.putString(this.getTime(), 8, super.getEncoder());
			in.put(this.getQuery_Type());
			in.putString(this.getQuery_Code(), 10, super.getEncoder());
			in.putString(this.getReserve(), 8, super.getEncoder());
		}catch(CharacterCodingException e) {
			logger.error("封装MsgQuery二进制数组失败。"+e.getMessage());
		}		
		return in;
	}
	
	public static MsgQuery toObject(MsgHead msgHead,IoBuffer in){
		MsgQuery msgQuery=new MsgQuery();
		try {
			msgQuery.setTotalLength(msgHead.getTotalLength());
			msgQuery.setCommandId(msgHead.getCommandId());
			msgQuery.setSequenceId(msgHead.getSequenceId());
			msgQuery.setTime(in.getString(8, getDecoder()));
			msgQuery.setQuery_Type(in.get());
			msgQuery.setQuery_Code(in.getString(10, getDecoder()));
			msgQuery.setReserve(in.getString(8, getDecoder()));
			logger.debug("smsBoss-MsgQuery.toObject:"+ JsonMapper.toJsonString(msgQuery));
		} catch (CharacterCodingException e) {
			logger.error("MsgConnect二进制数组转MsgConnect失败。"+e.getMessage());
		}
		return msgQuery;
	}	
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public byte getQuery_Type() {
		return query_Type;
	}
	public void setQuery_Type(byte queryType) {
		query_Type = queryType;
	}
	public String getQuery_Code() {
		return query_Code;
	}
	public void setQuery_Code(String queryCode) {
		query_Code = queryCode;
	}

	public String getReserve() {
		return reserve;
	}
	public void setReserve(String reserve) {
		this.reserve = reserve;
	}
	
	
}
