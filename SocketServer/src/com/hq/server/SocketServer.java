package com.hq.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TimerTask;

import javax.management.timer.Timer;

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
