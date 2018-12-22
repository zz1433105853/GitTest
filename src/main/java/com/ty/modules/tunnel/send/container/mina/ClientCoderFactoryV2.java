package com.ty.modules.tunnel.send.container.mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;

/**
 * 
* <p>Title: </p>
* <p>Description:HCoderFactory</p> 
* <p>Company:天元科技</p>
* @author:jiaohj 
* @date:2016下午05:21:26
 */
public class ClientCoderFactoryV2 implements ProtocolCodecFactory {
	private final AbstractClientEncoder encoder;
	private final AbstractClientDecoder decoder;

	public ClientCoderFactoryV2() {
		this(Charset.defaultCharset());
	}

	public ClientCoderFactoryV2(Charset charSet) {
		this.encoder = new ClientEncoderV2(charSet);
		this.decoder = new ClientDecoderV2(charSet);
	}	

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

}
