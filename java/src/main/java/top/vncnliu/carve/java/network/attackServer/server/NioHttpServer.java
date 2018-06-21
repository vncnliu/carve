package top.vncnliu.carve.java.network.attackServer.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * add description
 * created at 2016/8/28 16:19
 * @author vncnliu
 * @version default 1.0.0
 */

public class NioHttpServer {

    //通道管理器
    private Selector selector;

    //获取一个ServerSocket通道，并初始化通道
    public NioHttpServer init(int port) throws IOException {
        //获取一个ServerSocket通道
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
        //获取通道管理器
        selector = Selector.open();
        //将通道管理器与通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，
        //只有当该事件到达时，Selector.select()会返回，否则一直阻塞。
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        return this;
    }


    public void listen() throws IOException, InterruptedException {
        System.out.println("服务器端启动成功");

        //使用轮询访问selector
        while (!Thread.interrupted()) {
            //当有注册的事件到达时，方法返回，否则阻塞。
            selector.select();
            System.out.println("--------OP_ACCEPT------------");
            //获取selector中的迭代器，选中项为注册的事件
            Iterator<SelectionKey> ite = selector.selectedKeys().iterator();

            while (ite.hasNext()) {
                SelectionKey key = ite.next();
                //删除已选key，防止重复处理
                ite.remove();
                //客户端请求连接事件
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    //获得客户端连接通道
                    SocketChannel channel = server.accept();
                    channel.configureBlocking(false);
                    //在与客户端连接成功后，为客户端通道注册SelectionKey.OP_READ事件。
                    channel.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端请求连接事件");
                } else if (key.isReadable()) {//有可读数据事件
                    System.out.println("有可读数据事件");
                    //获取客户端传输数据可读取消息通道。
                    SocketChannel channel = (SocketChannel) key.channel();
                    //获取客户端传输数据可读取消息通道。
                    try {
                        AtomicInteger crlf = new AtomicInteger(0);
                        ByteBuffer buffer = ByteBuffer.allocate(128);
                        StringBuilder line = new StringBuilder();
                        int read = 0;
                        while (read >= 0) {
                            //创建读取数据缓冲器
                            read = channel.read(buffer);
                            if (read == 0) break;
                            buffer.flip();
                            CharBuffer result = Charset.forName("UTF-8").decode(buffer);
                            buffer.clear();
                            for (char c : result.array()) {
                                if ((c == '\n') || (c == '\r')) {
                                    if (crlf.getAndIncrement() == 3) {
                                        System.out.println(line.toString());
                                        String echo = "HTTP/1.1 200 \r\n" +
                                                "Content-Type: text/html\r\n" +
                                                "Content-Length: 109\r\n" +
                                                "\r\n" +
                                                "<html>" +
                                                "<head>" +
                                                "<title>hello</title>" +
                                                "</head>" +
                                                "<body>" +
                                                "<center>" +
                                                "<b1>Welcome to my home page.</b1>" +
                                                "</center>" +
                                                "</body>" +
                                                "</html>";
                                        channel.write(ByteBuffer.wrap(echo.getBytes()));
                                        break;
                                    }
                                } else {
                                    crlf.set(0);
                                }
                            }
                            line.append(result.array());
                        }
                        if (read == -1) {
                            //System.out.println("连接准备关闭"+System.currentTimeMillis());
                            //Thread.sleep(10000);
                            channel.close();
                            System.out.println("连接关闭"+System.currentTimeMillis());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
