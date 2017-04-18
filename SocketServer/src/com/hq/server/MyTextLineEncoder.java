package com.hq.server;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * 编码器
 * 
 * @author
 *
 */
public class MyTextLineEncoder implements ProtocolEncoder {

	@Override
	public void dispose(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		String s = null;
		if (message instanceof String) {
			s = (String) message;
		}
		if (s != null) {
			CharsetEncoder charsetEncoder = (CharsetEncoder) session.getAttribute("encoder");
			if (charsetEncoder == null) {

				charsetEncoder = Charset.defaultCharset().newEncoder();
			}
			// 1.开辟内存
			IoBuffer ioBuffer = IoBuffer.allocate(s.length());
			ioBuffer.setAutoExpand(true);
			// 2.
			ioBuffer.putString(s, charsetEncoder);
			// 3.
			ioBuffer.flip();
			out.write(ioBuffer);
		}
	}

}
