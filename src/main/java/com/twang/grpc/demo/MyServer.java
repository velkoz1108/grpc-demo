package com.twang.grpc.demo;

import com.google.protobuf.Timestamp;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class MyServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9090)
                .addService(new HelloServiceImpl())
                .addService(new StockQuoteProviderImpl())
                .build().start();
        System.out.println("server = " + server.toString());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("server is shutdown ...");
            server.shutdown();
        }));
        server.awaitTermination();
    }

    static class HelloServiceImpl extends com.twang.grpc.demo.HelloServiceGrpc.HelloServiceImplBase {
        @Override
        public void hello(com.twang.grpc.demo.HelloRequest request, StreamObserver<com.twang.grpc.demo.HelloResponse> responseObserver) {
            System.out.println("request.getFirstName() = " + request.getFirstName());
            System.out.println("request.getLastName() = " + request.getLastName());
            com.twang.grpc.demo.HelloResponse response = com.twang.grpc.demo.HelloResponse.newBuilder()
                    .setGreeting("hello from grpc server")
                    .setTime(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    static class StockQuoteProviderImpl extends com.twang.grpc.demo.StockQuoteProviderGrpc.StockQuoteProviderImplBase {
        @Override
        public void serverSideStreamingGetListStockQuotes(com.twang.grpc.demo.Stock request,
                                                          StreamObserver<com.twang.grpc.demo.StockQuote> responseObserver) {
            for (int i = 1; i <= 5; i++) {
                com.twang.grpc.demo.StockQuote stockQuote = com.twang.grpc.demo.StockQuote.newBuilder()
                        .setPrice(fetchStockPriceBid(request))
                        .setOfferNumber(i)
                        .setDescription("Price for stock:" + request.getTickerSymbol())
                        .build();
                responseObserver.onNext(stockQuote);
            }
            responseObserver.onCompleted();
        }

        @Override
        public StreamObserver<com.twang.grpc.demo.Stock> clientSideStreamingGetStatisticsOfStocks(
                StreamObserver<com.twang.grpc.demo.StockQuote> responseObserver) {
            return new StreamObserver<com.twang.grpc.demo.Stock>() {
                int count;
                double price = 0.0;
                StringBuffer sb = new StringBuffer();

                @Override
                public void onNext(com.twang.grpc.demo.Stock stock) {
                    count++;
                    price = +fetchStockPriceBid(stock);
                    sb.append(":")
                            .append(stock.getTickerSymbol());
                }

                @Override
                public void onCompleted() {
                    responseObserver.onNext(com.twang.grpc.demo.StockQuote.newBuilder()
                            .setPrice(price / count)
                            .setDescription("Statistics-" + sb.toString())
                            .build());
                    responseObserver.onCompleted();
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("error:" + t.getMessage());
                }
            };
        }

        @Override
        public StreamObserver<com.twang.grpc.demo.Stock> bidirectionalStreamingGetListsStockQuotes(
                StreamObserver<com.twang.grpc.demo.StockQuote> responseObserver) {
            return new StreamObserver<com.twang.grpc.demo.Stock>() {
                @Override
                public void onNext(com.twang.grpc.demo.Stock request) {

                    for (int i = 1; i <= 5; i++) {

                        com.twang.grpc.demo.StockQuote stockQuote = com.twang.grpc.demo.StockQuote.newBuilder()
                                .setPrice(fetchStockPriceBid(request))
                                .setOfferNumber(i)
                                .setDescription("Price for stock:" + request.getTickerSymbol())
                                .build();
                        responseObserver.onNext(stockQuote);
                    }
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("error:" + t.getMessage());
                }
            };
        }

        private static double fetchStockPriceBid(com.twang.grpc.demo.Stock stock) {

            return stock.getTickerSymbol()
                    .length()
                    + ThreadLocalRandom.current()
                    .nextDouble(-0.1d, 0.1d);
        }
    }
}
