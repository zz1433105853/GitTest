package com.ty.modules.tunnel.send.container.entity.cmpp;

import com.ty.common.mapper.JsonMapper;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import java.nio.charset.CharacterCodingException;


/**
 * 
* <p>Title: </p>
* <p>Description:SP向ISMG查询发送短信状态响应结构体定义</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午05:10:15
 */
public class MsgQueryResp extends MsgHead {
	private static Logger logger= Logger.getLogger(MsgQueryResp.class);
	private String time = "";
	private byte query_Type = 0x00;
	private String query_Code = "";
	private long mT_Tlmsg;
	private long mT_Tlusr;
	private long mT_Scs;
	private long mT_Wt;
	private long mT_Fl;
	private long mO_Scs;
	private long mO_Wt;
	private long mO_Fl;
	
	public static MsgQueryResp toObject(MsgHead msgHead,IoBuffer in){
		MsgQueryResp msgQueryResp=new MsgQueryResp();
		try {
			msgQueryResp.setTotalLength(msgHead.getTotalLength());
			msgQueryResp.setCommandId(msgHead.getCommandId());
			msgQueryResp.setSequenceId(msgHead.getSequenceId());
			msgQueryResp.setTime(in.getString(8, getDecoder()));
			msgQueryResp.setQuery_Type(in.get());
			msgQueryResp.setQuery_Code(in.getString(10, getDecoder()));
			msgQueryResp.setmT_Tlmsg(in.getUnsignedInt());
			msgQueryResp.setmT_Tlusr(in.getUnsignedInt());
			msgQueryResp.setmT_Scs(in.getUnsignedInt());
			msgQueryResp.setmT_Wt(in.getUnsignedInt());
			msgQueryResp.setmT_Fl(in.getUnsignedInt());
			msgQueryResp.setmO_Scs(in.getUnsignedInt());
			msgQueryResp.setmO_Wt(in.getUnsignedInt());
			msgQueryResp.setmO_Fl(in.getUnsignedInt());
			logger.debug("smsBoss-MsgQueryResp.toObject:"+ JsonMapper.toJsonString(msgQueryResp));
		}catch(CharacterCodingException e) {
			logger.error("封装MsgQueryResp二进制数组失败。"+e.getMessage());
		}
		return msgQueryResp;
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
	public long getmT_Tlmsg() {
		return mT_Tlmsg;
	}

	public void setmT_Tlmsg(long mTTlmsg) {
		mT_Tlmsg = mTTlmsg;
	}
	public long getmT_Tlusr() {
		return mT_Tlusr;
	}
	public void setmT_Tlusr(long mTTlusr) {
		mT_Tlusr = mTTlusr;
	}
	public long getmT_Scs() {
		return mT_Scs;
	}

	public void setmT_Scs(long mTScs) {
		mT_Scs = mTScs;
	}

	public long getmT_Wt() {
		return mT_Wt;
	}

	public void setmT_Wt(long mTWt) {
		mT_Wt = mTWt;
	}

	public long getmT_Fl() {
		return mT_Fl;
	}

	public void setmT_Fl(long mTFl) {
		mT_Fl = mTFl;
	}

	public long getmO_Scs() {
		return mO_Scs;
	}

	public void setmO_Scs(long mOScs) {
		mO_Scs = mOScs;
	}

	public long getmO_Wt() {
		return mO_Wt;
	}

	public void setmO_Wt(long mOWt) {
		mO_Wt = mOWt;
	}

	public long getmO_Fl() {
		return mO_Fl;
	}

	public void setmO_Fl(long mOFl) {
		mO_Fl = mOFl;
	}
	
}
