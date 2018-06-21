package com.sangame.jjhs.grpc.front;

import com.sangame.jjhs.grpc.LoginGrpc;
import com.sangame.jjhs.grpc.TokenLoginReply;
import com.sangame.jjhs.grpc.TokenLoginRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

/**
 * add description<br>
 * created on 2018/2/6<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class FrontServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(38810)
                .addService(new LoginWithTokenImpl())
                .build()
                .start();
        Runtime.getRuntime().addShutdownHook(
                new Thread(server::shutdown));

        if (server != null) {
            server.awaitTermination();
        }
    }

    static class LoginWithTokenImpl extends LoginGrpc.LoginImplBase {

        @Override
        public void loginWithToken(TokenLoginRequest request, StreamObserver<TokenLoginReply> responseObserver) {
            System.out.println(request.getToken());
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                responseObserver.onNext(
                        TokenLoginReply.newBuilder().setCode(0).setMessage("success").build());
            }
            responseObserver.onCompleted();
        }
    }
}
