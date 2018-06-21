package top.vncnliu.carve.java.network.attackServer.handle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * add description
 * created at 2016/8/28 15:48
 * @author vncnliu
 * @version default 1.0.0
 */

public class BioHttpHandle implements Runnable {

    Socket socket;

    public BioHttpHandle(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        StringBuilder echo = new StringBuilder();
        BufferedReader in;
        PrintWriter out;
        int contentSize = 0;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);
            String body;
            while (true) {
                //if(socket.isClosed()) return;
                try {
                    body = in.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                System.out.println(body);
                if (body == null) {
                    System.out.println("shutdownOutput");
                    socket.shutdownOutput();
                    break;
                }
                if ("".equals(body)) {
                    //Content-Length
                    char[] content = new char[contentSize];
                    in.read(content);
                    //System.out.println(content);
                    echo.append("HTTP/1.1 200 \r\n");
                    //echo.append("Connection: close\r\n");
                    echo.append("Content-Type: text/html\r\n");
                    echo.append("Content-Length: 109\r\n");
                    echo.append("\r\n");
                    echo.append("<html>");
                    echo.append("<head>");
                    echo.append("<title>hello</title>");
                    echo.append("</head>");
                    echo.append("<body>");
                    echo.append("<center>");
                    echo.append("<b1>Welcome to my home page.</b1>");
                    echo.append("</center>");
                    echo.append("</body>");
                    echo.append("</html>");
                    out.println(echo.toString());
                }
                if ("Content-Length".equals(body.split(":")[0])) {
                    contentSize = Integer.parseInt(body.split(":")[1].trim());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                socket.shutdownOutput();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                System.out.println("shutdownOutput");
                socket.shutdownInput();
                System.out.println("close");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
