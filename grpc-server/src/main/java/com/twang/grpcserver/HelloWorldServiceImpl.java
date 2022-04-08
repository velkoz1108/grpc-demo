package com.twang.grpcserver;

import com.twang.proto.Greeting;
import com.twang.proto.HelloWorldServiceGrpc;
import com.twang.proto.Person;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class HelloWorldServiceImpl extends HelloWorldServiceGrpc.HelloWorldServiceImplBase {
    @Override
    public void sayHello(Person request, StreamObserver<Greeting> responseObserver) {
        Greeting reply = Greeting.newBuilder().setMessage("Hello ==> " + request.getFirstName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
