package com.ty.modules.tunnel.send.container.mina;

import org.apache.mina.core.session.IoSession;
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
public class ClientCoderFactory extends AbstractClientCoderfactory {
	private final AbstractClientEncoder encoder;
	private final AbstractClientDecoder decoder;
	
	public ClientCoderFactory() {
		this(Charset.defaultCharset());
	}

	public ClientCoderFactory(Charset charSet) {
		this.encoder = new ClientEncoder(charSet);
		this.decoder = new ClientDecoder(charSet);
	}	

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

}
