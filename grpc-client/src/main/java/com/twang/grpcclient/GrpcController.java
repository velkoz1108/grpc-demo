package com.twang.grpcclient;

import com.twang.proto.HelloWorld;
import com.twang.proto.HelloWorldServiceGrpc;
import com.twang.proto.Person;
import io.grpc.CallOptions;
import io.grpc.Metadata;
import net.devh.boot.grpc.client.channelfactory.GrpcChannelFactory;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grpc")
public class GrpcController {

    @GrpcClient("gRPC server name")
    private HelloWorldServiceGrpc.HelloWorldServiceBlockingStub stub;


    @GetMapping("/hi")
    public String hi(@RequestParam String firstName, @RequestParam String lastName) {

        Person person = Person.newBuilder().setFirstName(firstName).setLastName(lastName).build();

        String message = stub.sayHello(person).getMessage();
        System.out.println("message from server = " + message);
        return message;
    }

    @Autowired
    private GrpcChannelFactory grpcChannelFactory;


}
