package top.vncnliu.carve.java.network.attackServer;

import top.vncnliu.carve.java.network.attackServer.server.BioHttpServer;
import top.vncnliu.carve.java.network.attackServer.server.NettyHttpServer;
import top.vncnliu.carve.java.network.attackServer.server.NioHttpServer;

/**
 * add description
 * created on 2016/8/10 15:11.
 *
 * @author vncnliu
 * @version default 1.0.0
 */
public class HttpServer {

    public static void main(String[] args) {
        try {
            if(args.length==0){
                new NettyHttpServer().init(8060);
            }
            else if("bio".equals(args[0])){
                new BioHttpServer().init(8080);
            }else if("nio".equals(args[0])){
                new NioHttpServer().init(8070).listen();
            }
            System.err.println("Open your web browser and navigate");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
