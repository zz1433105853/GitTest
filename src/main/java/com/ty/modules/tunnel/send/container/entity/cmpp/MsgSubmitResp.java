package com.ty.modules.tunnel.send.container.entity.cmpp;

import com.ty.common.mapper.JsonMapper;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

/**
 * 
* <p>Title: </p>
* <p>Description:ISMG以CMPP_SUBMIT_RESP消息响应</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午03:08:09
 */
public class MsgSubmitResp extends MsgHead {
	private static Logger logger= Logger.getLogger(MsgSubmitResp.class);
	private long msgId;
	private long result;//结果 0：正确 1：消息结构错 2：命令字错 3：消息序号重复 4：消息长度错 5：资费代码错 6：超过最大信息长 7：业务代码错 8：流量控制错 9：本网关不负责服务此计费号码 10：Src_Id错误 11：Msg_src错误 12：Fee_terminal_Id错误 13：Dest_terminal_Id错误
	
	public static MsgSubmitResp toObject(MsgHead msgHead,IoBuffer in){
		MsgSubmitResp msgSubmitResp=new MsgSubmitResp();
		msgSubmitResp.setTotalLength(msgHead.getTotalLength());
		msgSubmitResp.setCommandId(msgHead.getCommandId());
		msgSubmitResp.setSequenceId(msgHead.getSequenceId());
		msgSubmitResp.setMsgId(in.getLong());
		msgSubmitResp.setResult(in.getUnsignedInt());
		logger.debug("smsBoss-MsgSubmitResp.toObject:"+ JsonMapper.toJsonString(msgSubmitResp));
		return msgSubmitResp;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
	public long getResult() {
		return result;
	}
	public void setResult(long result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "MsgSubmitResp{" +
				"msgId=" + msgId +
				", result=" + result +
				'}';
	}
}
