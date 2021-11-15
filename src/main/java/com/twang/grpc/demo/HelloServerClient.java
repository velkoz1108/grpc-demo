package com.twang.grpc.demo;

import io.grpc.ManagedChannelBuilder;

public class HelloServerClient {
    public static void main(String[] args) {
        HelloServiceGrpc.HelloServiceBlockingStub blockingStub = HelloServiceGrpc.newBlockingStub(
                ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build());

        com.twang.grpc.demo.HelloResponse response = blockingStub.hello(HelloRequest.newBuilder().setFirstName("eden").setLastName("wang").build());

        System.out.println("response.getGreeting() = " + response.getGreeting());
        System.out.println("response.getTime() = " + response.getTime());

    }
}
