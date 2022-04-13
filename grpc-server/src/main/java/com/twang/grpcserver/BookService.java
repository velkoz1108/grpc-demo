package com.twang.grpcserver;

import com.twang.proto.Book;
import com.twang.proto.BookServiceGrpc;
import com.twang.proto.SearchRequest;
import com.twang.proto.SearchResponse;
import com.twang.proto.StreamResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.concurrent.TimeUnit;

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

    @Override
    public void queryBookStream(SearchRequest request, StreamObserver<StreamResponse> responseObserver) {
        StreamResponse book1 = StreamResponse.newBuilder()
                .setBook(Book.newBuilder()
                        .setBookName("Java入门")
                        .setBookType(Book.BookType.JAVA)
                        .setAuthor("Gosling")
                        .setPrice(99.99f)
                        .setRemark("java是最好的语言")
                        .setSaleStatus(true)
                        .build())
                .build();
        responseObserver.onNext(book1);

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        StreamResponse book2 = StreamResponse.newBuilder()
                .setBook(Book.newBuilder()
                        .setBookName("CPP第五版")
                        .setBookType(Book.BookType.CPP)
                        .setAuthor("cpp")
                        .setPrice(199.99f)
                        .setRemark("cpp有点难")
                        .setSaleStatus(true)
                        .build())
                .build();
        responseObserver.onNext(book2);

        responseObserver.onCompleted();
    }
}
