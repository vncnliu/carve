package top.vncnliu.carve.java.network.bio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * add description<br>
 * created on 2018/3/21<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class QieSOLingerClient {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        int serverPort = Integer.parseInt(args[0]);
        new Thread(new Client(serverPort)).start();
        new Thread(new Client(serverPort)).start();
    }

    static class Client implements Runnable {

        private int serverPort;

        public Client(int serverPort) {
            this.serverPort = serverPort;
        }

        @Override
        public void run() {
            try {
                Socket socket = new Socket();
                socket.setSoLinger(true,10);
                socket.connect(new InetSocketAddress("127.0.0.1",serverPort));
                LOGGER.info("socket option linger:"+socket.getSoLinger());
                StringBuilder strB = new StringBuilder();
                for(int i = 0 ; i < 500; i++ ){
                    strB.append("hello");
                }
                byte[] request = strB.toString().getBytes("utf-8");
                socket.getOutputStream().write(request);
                System.out.println("write "+request.length);
                System.out.println(System.currentTimeMillis());
                socket.close();
                System.out.println(System.currentTimeMillis());
            } catch (Exception e){
                LOGGER.error(e.getMessage(),e);
            }
        }
    }
}
