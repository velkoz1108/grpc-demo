package com.twang.grpcserver;

import com.twang.proto.Book;
import com.twang.proto.BookServiceGrpc;
import com.twang.proto.SearchRequest;
import com.twang.proto.SearchResponse;
import com.twang.proto.StreamRequest;
import com.twang.proto.StreamResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @Override
    public StreamObserver<StreamRequest> clientStream(StreamObserver<SearchResponse> responseObserver) {
        return new StreamObserver<StreamRequest>() {
            List<Integer> bookIds = new ArrayList<>();

            @Override
            public void onNext(StreamRequest streamRequest) {
                System.out.println("streamRequest = " + streamRequest.toString());
                int bookId = streamRequest.getBookId();
                bookIds.add(bookId);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("throwable = " + throwable);
            }

            @Override
            public void onCompleted() {
                System.out.println(" 客户端已经发送完数据了 onCompleted ===> " + new Date());
                //向客户端发送数据
                SearchResponse.Builder builder = SearchResponse.newBuilder();
                for (int i = 0; i < bookIds.size(); i++) {
                    builder.addBooks(Book.newBuilder()
                            .setBookName("Book-" + (i + 1))
                            .setBookType(Book.BookType.GO)
                            .setPrice(100 + i)
                            .setSaleStatus(false)
                            .setAuthor("author-" + (i + 100))
                            .build());
                }
                SearchResponse response = builder.setCode(200).setMessage("success").setBookCount(bookIds.size()).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<StreamRequest> biStream(StreamObserver<StreamResponse> responseObserver) {
        return new StreamObserver<StreamRequest>() {
            List<Integer> bookIds = new ArrayList<>();

            @Override
            public void onNext(StreamRequest streamRequest) {
                System.out.println("双向流 收到服务端消息 = " + streamRequest.toString());
                int bookId = streamRequest.getBookId();
                bookIds.add(bookId);

                //也可以随时响应
                System.out.println("双向流 先应付一下客户端 = " + new Date());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                responseObserver.onNext(StreamResponse.newBuilder().setBook(Book.newBuilder().setBookName("empty").build()).build());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("throwable = " + throwable);
            }

            @Override
            public void onCompleted() {
                System.out.println(" 客户端已经发送完数据了 onCompleted ===> " + new Date());

                for (int i = 0; i < bookIds.size(); i++) {


                    StreamResponse response = StreamResponse.newBuilder().setBook(Book.newBuilder()
                            .setBookName("Book-" + (i + 1))
                            .setBookType(Book.BookType.GO)
                            .setPrice(100 + i)
                            .setSaleStatus(false)
                            .setAuthor("author-" + (i + 100))
                            .build()).build();
                    //向客户端发送数据
                    System.out.println(" 服务端开始向客户端发送消息 ===>" + response);
                    responseObserver.onNext(response);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                responseObserver.onCompleted();
                System.out.println(" 服务端发送消息结束 ===>" + new Date());
            }
        };
    }
}
