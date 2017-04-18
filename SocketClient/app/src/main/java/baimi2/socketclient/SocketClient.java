package baimi2.socketclient;

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
        try {
            socket = new Socket("127.0.0.1", 9898);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            inputReader = new BufferedReader(new InputStreamReader(System.in));
            String inputContent;
            while (!(inputContent=inputReader.readLine()).equals("bye")){
                writer.write(inputContent+"\n");
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

}




