package com.ty.modules.tunnel.send.container.entity.cmpp;

import com.ty.common.mapper.JsonMapper;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

/**
 * 
* <p>Title: </p>
* <p>Description:ISMG向SP送交短信响应</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午03:01:56
 */
public class MsgDeliverResp extends MsgHead {
	private static Logger logger= Logger.getLogger(MsgDeliverResp.class);
	private long msg_Id;//信息标识（CMPP_DELIVER中的Msg_Id字段）
	private long result;//结果 0：正确 1：消息结构错 2：命令字错 3：消息序号重复 4：消息长度错 5：资费代码错 6：超过最大信息长 7：业务代码错8: 流量控制错9~ ：其他错误

	public IoBuffer toIoBuffer(){
		IoBuffer in= IoBuffer.allocate(200).setAutoExpand(true);
		try {
			in.putUnsignedInt(this.getTotalLength());
			in.putUnsignedInt(this.getCommandId());
			in.putUnsignedInt(this.getSequenceId());

			in.putLong(this.msg_Id);

			in.putUnsignedInt(this.result);

		} catch (Exception e) {
			logger.error("封装MsgDeliverResp二进制数组失败。"+e.getMessage());
		}
		return in;
	}

	public static MsgDeliverResp toObject(MsgHead msgHead,IoBuffer in){
		MsgDeliverResp msgDeliverResp=new MsgDeliverResp();
		msgDeliverResp.setTotalLength(msgHead.getTotalLength());
		msgDeliverResp.setCommandId(msgHead.getCommandId());
		msgDeliverResp.setSequenceId(msgHead.getSequenceId());	
		msgDeliverResp.setMsg_Id(in.getLong());
		msgDeliverResp.setResult(in.getUnsignedInt());
		logger.debug("smsBoss-MsgDeliverResp.toObject:"+ JsonMapper.toPrettyJsonStr(msgDeliverResp));
		return msgDeliverResp;
	}


	
	public long getMsg_Id() {
		return msg_Id;
	}

	public void setMsg_Id(long msg_Id) {
		this.msg_Id = msg_Id;
	}

	public long getResult() {
		return result;
	}

	public void setResult(long result) {
		this.result = result;
	}
}
