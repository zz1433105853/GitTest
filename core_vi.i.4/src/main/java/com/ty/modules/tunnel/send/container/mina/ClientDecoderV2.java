package com.ty.modules.tunnel.send.container.mina;

import com.ty.common.mapper.JsonMapper;
import com.ty.common.utils.StringUtils;
import com.ty.modules.tunnel.send.container.entity.cmpp.*;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 
* <p>Title: </p>
* <p>Description:解码工具类</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午05:26:18
 */
public class ClientDecoderV2 extends AbstractClientDecoder{
	private static Logger logger= Logger.getLogger(ClientDecoderV2.class);
	private final Charset charset;

	public ClientDecoderV2(Charset charset) {
		this.charset = charset;
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		CharsetDecoder de = charset.newDecoder();
		MsgHead msgHead = MsgHead.toObject(in);
		switch ((int)msgHead.getCommandId()) {
		case MsgCommand.CMPP_CONNECT_RESP:
			MsgConnectRespV2 msgConnectResp = MsgConnectRespV2.toObject(msgHead, in);
			out.write(msgConnectResp);
			logger.debug(StringUtils.builderString("ClientDecoder.doDecode():", JsonMapper.toPrettyJsonStr(msgConnectResp)));
			break;

		case MsgCommand.CMPP_TERMINATE_RESP:
			out.write(msgHead);
			logger.debug(StringUtils.builderString("ClientDecoder.doDecode():", JsonMapper.toPrettyJsonStr(msgHead)));
			break;
			
		case MsgCommand.CMPP_SUBMIT_RESP:
			MsgSubmitRespV2 msgSubmitResp = MsgSubmitRespV2.toObject(msgHead, in);
			out.write(msgSubmitResp);
			logger.debug(StringUtils.builderString("ClientDecoder.doDecode():", JsonMapper.toPrettyJsonStr(msgSubmitResp)));
			break;
			
		case MsgCommand.CMPP_DELIVER:
			MsgDeliverV2 msgDeliver = MsgDeliverV2.toObject(msgHead, in);
			//MsgContainer.sendMsgDeliverResp(msgDeliver.getMsg_Id(), msgDeliver.getResult());
			out.write(msgDeliver);
			logger.debug(StringUtils.builderString("ClientDecoder.doDecode():", JsonMapper.toPrettyJsonStr(msgDeliver)));
			break;
			
		case MsgCommand.CMPP_QUERY_RESP:
			MsgQueryResp msgQueryResp = MsgQueryResp.toObject(msgHead, in);
			out.write(msgQueryResp);
			logger.debug(StringUtils.builderString("ClientDecoder.doDecode():", JsonMapper.toPrettyJsonStr(msgQueryResp)));
			break;
			
		case MsgCommand.CMPP_CANCEL_RESP:
			break;
			
		case MsgCommand.CMPP_ACTIVE_TEST_RESP:
			MsgActiveTestResp msgActiveTestResp = MsgActiveTestResp.toObject(msgHead, in);
			out.write(msgActiveTestResp);
			logger.debug(StringUtils.builderString("ClientDecoder.doDecode():", JsonMapper.toPrettyJsonStr(msgActiveTestResp)) );
			break;
		case MsgCommand.CMPP_ACTIVE_TEST:
			MsgActiveTest msgActiveTest = MsgActiveTest.toObject(msgHead,in);
			out.write(msgActiveTest);
			logger.debug(StringUtils.builderString("收到服务端链路检测", JsonMapper.toPrettyJsonStr(msgActiveTest)) );
			break;
		}
		return true;
	}

}
