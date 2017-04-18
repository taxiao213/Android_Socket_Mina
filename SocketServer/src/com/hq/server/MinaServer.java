package com.hq.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MinaServer {
	public static void main(String[] args) {
		try {
			//[1]创建
			NioSocketAcceptor acceptor = new NioSocketAcceptor();
			//[2]设置Handler
			acceptor.setHandler(new MyServerHandler());
			//[3]获取当前所有的拦截器,添加一个拦截器
			acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MyTextLineFactory()));;
			//[3.1]当读写进入空闲状态，会调用； 参数1：状态；参数2：时间
			//acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 5);
			//[4]绑定端口
			acceptor.bind(new InetSocketAddress(9898));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
