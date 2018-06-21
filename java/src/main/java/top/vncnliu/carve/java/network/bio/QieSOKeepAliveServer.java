package top.vncnliu.carve.java.network.bio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * add description<br>
 * created on 2018/3/20<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class QieSOKeepAliveServer {

    private static final Logger LOGGER = LogManager.getLogger(QieSOKeepAliveServer.class);

    public static void main(String[] args) {
        int serverPort = 18080;
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            new Thread(() ->{
                while (!Thread.interrupted()){
                    while (true){
                        try {
                            Socket socket = serverSocket.accept();
                            socket.setKeepAlive(true);
                            InputStream inputStream = socket.getInputStream();
                            byte[] readBuf = new byte[1024];
                            while (inputStream.read(readBuf)!=-1){
                                System.out.println(new String(readBuf));
                            }
                            socket.close();
                        } catch (IOException e) {
                            LOGGER.error(e.getMessage(),e);
                            break;
                        }

                    }
                }
            }).start();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
        }
    }
}
