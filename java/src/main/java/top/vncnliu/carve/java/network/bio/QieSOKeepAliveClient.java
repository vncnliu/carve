package top.vncnliu.carve.java.network.bio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * add description<br>
 * created on 2018/3/20<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class QieSOKeepAliveClient {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host,port));
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("hello".getBytes());
            Thread.sleep(60000);
            socket.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
        }
    }
}
