package top.vncnliu.carve.java.network.attackServer.handle.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * add description
 * created at 2016/8/28 15:50
 * @author vncnliu
 * @version default 1.0.0
 */

public class ChildrenChannelHandle extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new NettyHttpHandle());
    }
}
