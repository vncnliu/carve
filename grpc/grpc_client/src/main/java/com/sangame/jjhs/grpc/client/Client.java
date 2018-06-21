package com.sangame.jjhs.grpc.client;

import com.sangame.jjhs.grpc.LoginGrpc;
import com.sangame.jjhs.grpc.TokenLoginReply;
import com.sangame.jjhs.grpc.TokenLoginRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * add description<br>
 * created on 2018/3/6<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class Client {

    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("127.0.0.1", 38810)
                .usePlaintext(true)
                .build();

        LoginGrpc.LoginStub asyncStub = LoginGrpc.newStub(managedChannel);

        StreamObserver<TokenLoginReply> responseObserver = new StreamObserver<TokenLoginReply>() {
            @Override
            public void onNext(TokenLoginReply summary) {
                System.out.println(summary);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("error");
            }

            @Override
            public void onCompleted() {
                System.out.println("Finished RecordRoute");
            }
        };

        TokenLoginRequest tokenLoginRequest = TokenLoginRequest.newBuilder().setToken("test").build();
        asyncStub.loginWithToken(tokenLoginRequest,responseObserver);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        responseObserver.onCompleted();
    }

}
