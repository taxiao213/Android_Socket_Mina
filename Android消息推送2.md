###第三章 XMPP协议解析
>中文名 可扩展通讯和表示协议 
>
>外文名 Extensible Messaging and Presence Protocol
>
>XMPP中定义了三个角色，客户端，服务器，网关。通信能够在这三者的任意两个之间双向发生。服务器同时承担了客户端信息记录，连接管理和信息的路由功能。网关承担着与异构即时通信系统的互联互通，异构系统可以包括SMS（短信），MSN，ICQ等。基本的网络形式是单客户端通过TCP/IP连接到单服务器，然后在之上传输XML

优点：
>开放性
>标准型
>可扩展xml格式
>跨平台

缺点：
>数据冗余
>不支持二进制数据

####XMPP的身份标识
>XMPP规定，每个客户端使用JID来作为身份标识的
>
>[user "@"]domain["/"resource]


####XML Stanza通用属性
* from
* to
* type
* id

####stream结构

	Client:
	<stream:stream
		to="example.com"
		xmlns="jabber:client"
		xmlns:stream="http://etherx.jabber.org/streams"
		version="1.0">

	Server:
	<stream:stream
		from="example.com"
		id="someid"
		xmlns="jabber:client"
		xmlns:stream="http://etherx.jabber.org/streams"
		version="1.0">

断开会话，发送标签

	Client:
	</stream:stream>

	Server:
	</stream:stream>

####presence结构,用户状态
	
	<presence
		from="jackson@gmail.com"
		to="jimmy@msn.com"
		type="unavailable">

>type参数：

* available 	在线
* unavailable	不在线
* subscribe 	订阅
* subscribed	授权订阅
* unsubscribe	取消订阅
* unsubscribed	授权取消订阅
* error 		错误

	<presence>
		<show>away</show>
		<status>Having a spot of tea</status>
	</presence>

>show参数：

* chat在线
* away暂离
* xa  长时间离开
* dnd 不想被人打扰

####message结构
	<message
		from="jackson@gmail.com"
		to="jimmy@msn.com"
		type="chat">
		<body>hello</body>
	</message>

>type参数：

* normal 	独立消息
* chat 		一对一会话
* groupchat 群聊会话
* headline	头条内容
* error		错误

####IQ结构
	<iq
		from="jackson@gmail.com"
		id="aa152"
		to="jackson@gmail.com"
		type="get">
		<query xmlns="jabber:iq:roster"/>
	</iq>

>type参数：

* get 		类似于http请求
* set 	
* result   	响应数据结果
* error		错误


-------------------------------
	<iq
		from="jackson@gmail.com"
		id="aa152"
		to="jackson@gmail.com"
		type="result">
		<query xmlns="jabber:iq:roster">
			<item jid="jimmy@msn.com"/>
			<item jid="tom@hotmail.com"/>
		</query>
	</iq>