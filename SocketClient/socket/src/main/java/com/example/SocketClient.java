package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by yin13 on 2017/4/16.
 */

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
            // 数据的丢失 '\n'
            int count = 0;
            while (!(inputContent = inputReader.readLine()).equals("bye")) {

                //writer.write(inputContent + "\n");
                writer.write(inputContent);
                if (count % 2 == 0) {
                    writer.write("\n");
                }
                count++;
                writer.flush();
              //  String response = reader.readLine();
              //  System.out.println(response);
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




