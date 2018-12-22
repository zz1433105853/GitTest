package com.ty.modules.tunnel.send.container.mina;

import com.ty.common.mapper.JsonMapper;
import com.ty.common.utils.StringUtils;
import com.ty.modules.tunnel.send.container.entity.cmpp.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;


/**
 * 
* <p>Title: </p>
* <p>Description:编码工具类</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午05:30:03
 */
public class ClientEncoderV2 extends AbstractClientEncoder {
	private final static Log logger = LogFactory.getLog(ClientEncoderV2.class);
	private final Charset charset;

	public ClientEncoderV2(Charset charset) {
		this.charset = charset;
	}
	
	@Override
	public void dispose(IoSession session) throws Exception {
		logger.info("执行：smsBoss项目-ClientEncoder类-dispose方法");
	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out)throws Exception {
		CharsetEncoder en = charset.newEncoder();
		MsgHead msgHead=(MsgHead) message;
		IoBuffer in=null;
		switch((int)msgHead.getCommandId()){
			case MsgCommand.CMPP_CONNECT:
				MsgConnectV2 msgConnect=(MsgConnectV2) message;
				logger.debug(StringUtils.builderString("ClientEncoder.encode():", JsonMapper.toPrettyJsonStr(msgConnect)));
				in=msgConnect.toIoBuffer();			
				break;
			case MsgCommand.CMPP_TERMINATE:
				logger.debug(StringUtils.builderString("ClientEncoder.encode():", JsonMapper.toPrettyJsonStr(msgHead)));
				in=msgHead.toIoBuffer();
				break;
			case MsgCommand.CMPP_SUBMIT:
				MsgSubmitV2 msgSubmit=(MsgSubmitV2) message;
				logger.debug(StringUtils.builderString("ClientEncoder.encode():", JsonMapper.toPrettyJsonStr(msgSubmit)));
				in=msgSubmit.toIoBuffer();
				break;
			case MsgCommand.CMPP_DELIVER_RESP:
				MsgDeliverRespV2 msgDeliverResp=(MsgDeliverRespV2) message;
				logger.debug(StringUtils.builderString("ClientEncoder.encode():", JsonMapper.toPrettyJsonStr(msgDeliverResp)));
				in=msgDeliverResp.toIoBuffer();
				break;
			case MsgCommand.CMPP_QUERY:
				MsgQuery msgQuery=(MsgQuery) message;
				logger.debug(StringUtils.builderString("ClientEncoder.encode():", JsonMapper.toPrettyJsonStr(msgQuery)));
				in=msgQuery.toIoBuffer();
				break;
			case MsgCommand.CMPP_CANCEL:
				break;
			case MsgCommand.CMPP_ACTIVE_TEST:
				logger.debug(StringUtils.builderString("ClientEncoder.encode():", JsonMapper.toPrettyJsonStr(msgHead)));
				in=msgHead.toIoBuffer();
				break;
			case MsgCommand.CMPP_ACTIVE_TEST_RESP:
				MsgActiveTestResp msgActiveTestResp=(MsgActiveTestResp) message;
				logger.debug(StringUtils.builderString("给服务端发送链路检测:", JsonMapper.toPrettyJsonStr(msgActiveTestResp)));
				in=msgActiveTestResp.toIoBuffer();
				break;
			
		}
		in.flip();
		out.write(in);	
	}

}
