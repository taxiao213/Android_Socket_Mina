package com.example;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

/**
 * Created by Han on 2017/4/17.
 */

public class MinaClient {

    public static void main(String[] args) {
        NioSocketConnector connector = new NioSocketConnector();
        connector.setHandler(new MyClientHandler());
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
        ConnectFuture future = connector.connect(new InetSocketAddress("127.0.0.1", 9898));
        //阻塞，等待客户端连接
        future.awaitUninterruptibly();
        IoSession session = future.getSession();

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        String inputContent;
        try {
            while (!(inputContent = inputReader.readLine()).equals("bye")){
                session.write(inputContent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
