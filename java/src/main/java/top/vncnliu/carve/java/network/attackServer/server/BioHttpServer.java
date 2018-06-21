package top.vncnliu.carve.java.network.attackServer.server;

import top.vncnliu.carve.java.network.attackServer.handle.BioHttpHandle;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * add description
 * created at 2016/8/28 16:19
 * @author vncnliu
 * @version default 1.0.0
 */

public class BioHttpServer {

    public void init(int port) {
        Thread thread = new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(false);
                serverSocket.bind(new InetSocketAddress(port));
                System.out.println(serverSocket.getReceiveBufferSize());
                System.out.println(serverSocket.getReuseAddress());
                System.out.println(serverSocket.getSoTimeout());
                while (!Thread.interrupted()){
                    Socket socket = serverSocket.accept();
                    System.out.println(socket.getKeepAlive());
                    System.out.println(socket.getOOBInline());
                    System.out.println(socket.getReceiveBufferSize());
                    System.out.println(socket.getReuseAddress());
                    System.out.println(socket.getSendBufferSize());
                    System.out.println(socket.getSoLinger());
                    System.out.println(socket.getTcpNoDelay());
                    new Thread(new BioHttpHandle(socket)).start();
                }
                System.out.println("thread inter");
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        });
        thread.start();
    }

}
