package com.twang.grpcserver;

import com.twang.proto.Book;
import com.twang.proto.BookServiceGrpc;
import com.twang.proto.SearchRequest;
import com.twang.proto.SearchResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class BookService extends BookServiceGrpc.BookServiceImplBase {

    @Override
    public void queryBook(SearchRequest request, StreamObserver<SearchResponse> responseObserver) {
        System.out.println("request = " + request.toString());

        SearchResponse response = SearchResponse.newBuilder()
                .addBooks(Book.newBuilder()
                        .setBookName("Java入门")
                        .setBookType(Book.BookType.JAVA)
                        .setAuthor("Gosling")
                        .setPrice(99.99f)
                        .setRemark("java是最好的语言")
                        .setSaleStatus(true)
                        .build())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
