package top.vncnliu.carve.java.network.bio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * add description<br>
 * created on 2018/3/21<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class QieSOLingerServer {

    private final static Logger LOGGER = LogManager.getLogger();
    public static void main(String[] args) {
        try {
            int serverPort = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(serverPort);
            serverSocket.setReceiveBufferSize(1024);
            new Thread(() ->{
                while (!Thread.interrupted()){
                    ExecutorService executorService = Executors.newCachedThreadPool();
                    while (true){
                        try {
                            Socket socket = serverSocket.accept();
                            LOGGER.info("accept socket");
                            executorService.submit(() ->{
                                try {
                                    InputStream inputStream = socket.getInputStream();
                                    byte[] readBuf = new byte[1024];
                                    while (inputStream.read(readBuf)!=-1){
                                        System.out.println(new String(readBuf));
                                    }
                                    socket.close();
                                } catch (Exception e){
                                    LOGGER.error(e.getMessage(),e);
                                }
                            });
                        } catch (IOException e) {
                            LOGGER.error(e.getMessage(),e);
                            break;
                        }
                    }
                }
            }).start();
        } catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
    }
}
