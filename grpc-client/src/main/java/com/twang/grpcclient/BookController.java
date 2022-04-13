package com.twang.grpcclient;

import com.google.common.util.concurrent.ListenableFuture;
import com.twang.proto.Book;
import com.twang.proto.BookServiceGrpc;
import com.twang.proto.HelloWorldServiceGrpc;
import com.twang.proto.SearchRequest;
import com.twang.proto.SearchResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequestMapping("/book")
@RestController
public class BookController {

    @GrpcClient("gRPC server name")
    private BookServiceGrpc.BookServiceFutureStub bookServiceFutureStub;

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
}
