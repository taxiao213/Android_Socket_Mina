##消息推送

###第一章 推送介绍

####长连接
长连接是指客户端和服务器之间始终建立一个通信连接，在连接没有中断之前，客户端和服务器之间可以随时进行通信。
>socket 推送

####短连接
短连接是指通讯双方有数据交互时，就建立一个连接，数据发送完成后，则断开此连接。
>http 轮询

问题：并发量，身份认证，断连掉线

推送平台：极光推送，个推，百度推送，小米推送

###第二章 Socket和Mina的使用

####服务器SocketServer代码
	public class SocketServer {
		BufferedWriter writer;
		BufferedReader reader;
		private Socket socket;
	
		public static void main(String[] args) {
			SocketServer socketserver = new SocketServer();
			socketserver.startServer();
	
		}
	
		private void startServer() {
			ServerSocket serverSocket;
			try {
				serverSocket = new ServerSocket(9898);
				System.out.println("server start...");
				while(true){
					socket = serverSocket.accept();
					managerConnection(socket);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	
		}
	
		public void managerConnection(final Socket socket) {
			new Thread(new Runnable() {
				public void run() {
					try {
						System.out.println("client " + socket.hashCode() + "  connected");
						reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		//					new java.util.Timer().schedule(new TimerTask() {
		//						@Override
		//						public void run() {
		//							try {
		//								System.out.println("heart beat once...");
		//								writer.write("heart beat once..." + "\n");
		//								writer.flush();
		//							} catch (IOException e) {
		//								e.printStackTrace();
		//							}
		//						}
		//					}, 2000, 2000);
						String receivedMsg;
						while ((receivedMsg = reader.readLine()) != null) {
							System.out.println(receivedMsg);
							writer.write("server reply:" + receivedMsg + "\n");
							writer.flush();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}finally{
						try {
							writer.close();
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}) {
			}.start();
		}
	}


####客户端SocketClient代码

	public class SocketClient {
	
	    private Socket socket;
	
	    public static void main(String[] args) {
	        SocketClient client = new SocketClient();
	        client.start();
	    }
	
	    private void start() {
	        BufferedReader inputReader = null;
	        BufferedWriter writer = null;
	        BufferedReader reader = null;
	        try {
	            socket = new Socket("127.0.0.1", 9898);
	            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	            inputReader = new BufferedReader(new InputStreamReader(System.in));
	            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	          //  startServerReplyListener(reader);
	            String inputContent;
	            while (!(inputContent = inputReader.readLine()).equals("bye")) {
	                writer.write(inputContent + "\n");
	                writer.flush();
	                String response = reader.readLine();
	                System.out.println(response);
	            }
	
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                writer.close();
	                inputReader.close();
	                socket.close();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	
	
	    }
	
	    public void startServerReplyListener(final BufferedReader reader) {
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	                String response;
	                try {
	                    while ((response = reader.readLine()) != null) {
	                        System.out.println(response);
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }).start();
	    }
	}



>javaNIO
>
>Mina和Netty都是Java领域高性能和高可伸缩性网络应用程序的网络应用框架，在实际生产应用中都是不错的佼佼者。

>Netty 是由JBOSS提供的一个java开源框架。Netty提供异步的、事件驱动的网络应用程序框架和工具，用以快速开发高性能、高可靠性的网络服务器和客户端程序。
也就是说，Netty 是一个基于NIO的客户，服务器端编程框架，使用Netty 可以确保你快速和简单的开发出一个网络应用，例如实现了某种协议的客户，服务端应用。Netty相当简化和流线化了网络应用的编程开发过程，例如，TCP和UDP的socket服务开发。

>“快速”和“简单”并不意味着会让你的最终应用产生维护性或性能上的问题。Netty 是一个吸收了多种协议的实现经验，这些协议包括FTP,SMTP,HTTP，各种二进制，文本协议，并经过相当精心设计的项目，最终，Netty 成功的找到了一种方式，在保证易于开发的同时还保证了其应用的性能，稳定性和伸缩性。
 
>Apache MINA(Multipurpose Infrastructure for Network Applications) 是 Apache 组织一个较新的项目，它为开发高性能和高可用性的网络应用程序提供了非常便利的框架。当前发行的 MINA 版本支持基于 Java NIO 技术的 TCP/UDP 应用程序开发、串口通讯程序（只在最新的预览版中提供），MINA 所支持的功能也在进一步的扩展中。

>目前正在使用 MINA 的软件包括有：Apache Directory Project、AsyncWeb、AMQP（Advanced Message Queuing Protocol）、RED5 Server（Macromedia Flash Media RTMP）、ObjectRADIUS、Openfire 等等。


>下载MINA jar包 [http://mina.apache.org/](http://mina.apache.org/)

>dist目录下的 mina-core导入
>
>lib目录下的slf4j-api导入
>
>Decoder解码器  Encoder编码器
>
>通道(Channel 类)：表示服务器和客户机之间的一种通信机制。 
选择器(Selector类)：是 Channel 的多路复用器。

###Mina框架讲解

####Mina客户端代码

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

-----------------------
####创建MyClientHandler
	public class MyClientHandler extends IoHandlerAdapter {
	
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			System.out.println("exceptionCaught");
		}
	
		@Override
		public void inputClosed(IoSession session) throws Exception {
			System.out.println("inputClosed");
		}
	
		/**
		 * 接受消息
		 */
		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {
			String string = (String) message;
			System.out.println("messageReceived..."+string);
	
		}
	
		/**
		 * 发送消息
		 */
		@Override
		public void messageSent(IoSession session, Object message) throws Exception {
			System.out.println("messageSent");
		}
	
		@Override
		public void sessionClosed(IoSession session) throws Exception {
			System.out.println("sessionClosed");
		}
	
		/**
		 * 1.创建session
		 */
		@Override
		public void sessionCreated(IoSession session) throws Exception {
			System.out.println("sessionCreated");
		}
	
		/**
		 * 空闲状态 服务器检测客户端是否断线，心跳
		 */
		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
			System.out.println("sessionIdle");
		}
	
		/**
		 * 2.session开启
		 */
		@Override
		public void sessionOpened(IoSession session) throws Exception {
			System.out.println("sessionOpened");
		}
		
	}


####Mina服务端代码

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


-------------------------

####创建MyServerHandler

	public class MyServerHandler extends IoHandlerAdapter {
	
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			System.out.println("exceptionCaught");
		}
	
		@Override
		public void inputClosed(IoSession session) throws Exception {
			System.out.println("inputClosed");
		}
	
		/**
		 * 接受消息
		 */
		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {
			String string = (String) message;
			System.out.println("messageReceived..."+string);
			session.write("server reply:"+string);
		}
	
		/**
		 * 发送消息
		 */
		@Override
		public void messageSent(IoSession session, Object message) throws Exception {
			System.out.println("messageSent");
		}
	
		@Override
		public void sessionClosed(IoSession session) throws Exception {
			System.out.println("sessionClosed");
		}
	
		/**
		 * 1.创建session
		 */
		@Override
		public void sessionCreated(IoSession session) throws Exception {
			System.out.println("sessionCreated");
		}
	
		/**
		 * 空闲状态 服务器检测客户端是否断线，心跳
		 */
		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
			System.out.println("sessionIdle");
		}
	
		/**
		 * 2.session开启
		 */
		@Override
		public void sessionOpened(IoSession session) throws Exception {
			System.out.println("sessionOpened");
		}
		
	}

----------------------------

####MyTextLineFactory

	/**
	 * 自定义类 MyTextLineFactory 编解码
	 * 
	 * @author aaa
	 *
	 */
	public class MyTextLineFactory implements ProtocolCodecFactory {
	
		private MyTextLineDecoder mDecoder;
		private MyTextLineCumulativeDecoder mCumulativeDecoder;
		private MyTextLineEncoder mEncoder;
	
		public MyTextLineFactory() {
			mDecoder = new MyTextLineDecoder();
			mEncoder = new MyTextLineEncoder();
			mCumulativeDecoder = new MyTextLineCumulativeDecoder();
		}
	
		@Override
		public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
			return mCumulativeDecoder;
		}
	
		@Override
		public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
			return mEncoder;
		}
	
	}

----------------------------------
####MyTextLineDecoder

	/**
	 * 解码器
	 * 
	 * @author aaa
	 *
	 */
	public class MyTextLineDecoder implements ProtocolDecoder {
	
		@Override
		public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
			int startPosition = in.position();
			while (in.hasRemaining()) {
				byte b = in.get();
				if (b == '\n') {
					int currentPosition = in.position();
					int limit = in.limit();
					in.position(startPosition);
					in.limit(currentPosition);
					// 返回截取之后的数据
					IoBuffer buf = in.slice();
					byte[] dest = new byte[buf.limit()];
					buf.get(dest);
					String str = new String(dest);
					out.write(str);
					in.position(currentPosition);
					in.limit(limit);
				}
			}
		}
	
		@Override
		public void dispose(IoSession arg0) throws Exception {
			// TODO Auto-generated method stub
	
		}
	
		@Override
		public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1) throws Exception {
			// TODO Auto-generated method stub
	
		}
	
	}

--------------------------------------
####MyTextLineEncoder

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


------------------------

####MyTextLineCumulativeDecoder 写出换行符的自定义类，避免数据丢失

	public class MyTextLineCumulativeDecoder extends CumulativeProtocolDecoder {
	
		/**
		 * 有换行符return TRUE;
		 */
		@Override
		protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
			int startPosition = in.position();
			while (in.hasRemaining()) {
				byte b = in.get();
				if (b == '\n') {
					int currentPosition = in.position();
					int limit = in.limit();
					in.position(startPosition);
					in.limit(currentPosition);
					// 返回截取之后的数据
					IoBuffer buf = in.slice();
					byte[] dest = new byte[buf.limit()];
					buf.get(dest);
					String str = new String(dest);
					out.write(str);
					in.position(currentPosition);
					in.limit(limit);
					return true;
				}
			}
			//定向到开始的位置
			in.position(startPosition);
			return false;
		}
	
	}