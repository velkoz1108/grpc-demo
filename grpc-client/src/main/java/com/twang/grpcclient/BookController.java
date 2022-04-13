package com.twang.grpcclient;

import com.google.common.util.concurrent.ListenableFuture;
import com.twang.proto.Book;
import com.twang.proto.BookServiceGrpc;
import com.twang.proto.HelloWorldServiceGrpc;
import com.twang.proto.SearchRequest;
import com.twang.proto.SearchResponse;
import com.twang.proto.StreamRequest;
import com.twang.proto.StreamResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequestMapping("/book")
@RestController
public class BookController {

    @GrpcClient("gRPC server name")
    private BookServiceGrpc.BookServiceFutureStub bookServiceFutureStub;

    @GrpcClient("gRPC server name")
    private BookServiceGrpc.BookServiceStub bookServiceStub;

    @GetMapping("/list")
    public String list() throws ExecutionException, InterruptedException, TimeoutException {
        SearchRequest request = SearchRequest.newBuilder()
                .setBookName("java")
                .build();
        ListenableFuture<SearchResponse> future = bookServiceFutureStub.queryBook(request);
        SearchResponse response = future.get(1, TimeUnit.MINUTES);
        System.out.println("response = " + response);
        for (Book book : response.getBooksList()) {
            System.out.println("book = " + book.toString());
            String bookName = new String(book.getBookName().getBytes(), Charset.forName("UTF-8"));
            System.out.println("bookName = " + bookName);
        }
        return response.toString();
    }


    @GetMapping("/stream")
    public String stream() throws ExecutionException, InterruptedException, TimeoutException {
        SearchRequest request = SearchRequest.newBuilder()
                .setBookName("java")
                .build();
        bookServiceStub.queryBookStream(request, new StreamObserver<StreamResponse>() {
            @Override
            public void onNext(StreamResponse streamResponse) {
                System.out.println("streamResponse = " + streamResponse.toString());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted =>> " + new Date());
            }
        });

        return "success";
    }

    @GetMapping("/clientStream")
    public String clientStream() {
        StreamObserver<StreamRequest> streamObserver = bookServiceStub.clientStream(new StreamObserver<SearchResponse>() {

            @Override
            public void onNext(SearchResponse searchResponse) {
                System.out.println("收到服务端响应 = " + searchResponse.toString());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("throwable = " + throwable);
            }

            @Override
            public void onCompleted() {
                System.out.println("服务端发送完毕 ===> " + new Date());
            }
        });

        for (int i = 0; i < 3; i++) {
            StreamRequest request = StreamRequest.newBuilder()
                    .setBookId(100 + i)
                    .build();
            System.out.println("客户端发送请求 = " + request.toString());
            streamObserver.onNext(request);
        }
        System.out.println("发送完毕 ===> " + new Date());
        streamObserver.onCompleted();

        return "success";
    }

    @GetMapping("/biStream")
    public String biStream() {
        StreamObserver<StreamRequest> streamObserver = bookServiceStub.biStream(new StreamObserver<StreamResponse>() {

            @Override
            public void onNext(StreamResponse streamResponse) {
                System.out.println("双向流 收到服务端响应 = " + streamResponse.toString());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("throwable = " + throwable);
            }

            @Override
            public void onCompleted() {
                System.out.println("双向流  服务端发送完毕 ===> " + new Date());
            }
        });

        for (int i = 0; i < 3; i++) {
            StreamRequest request = StreamRequest.newBuilder()
                    .setBookId(100 + i)
                    .build();
            System.out.println("双向流 客户端发送请求 = " + request.toString());
            streamObserver.onNext(request);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("双向流 客户端发送请求完毕 ===> " + new Date());
        streamObserver.onCompleted();

        return "success";
    }
}
