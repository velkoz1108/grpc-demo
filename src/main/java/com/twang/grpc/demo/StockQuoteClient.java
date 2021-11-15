package com.twang.grpc.demo;

import com.twang.grpc.demo.Stock;
import com.twang.grpc.demo.StockQuoteProviderGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Iterator;

public class StockQuoteClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
        StockQuoteProviderGrpc.StockQuoteProviderBlockingStub blockingStub = StockQuoteProviderGrpc.newBlockingStub(channel);
//        StockQuoteProviderGrpc.StockQuoteProviderStub newStub = StockQuoteProviderGrpc.newStub(channel);


        Iterator<com.twang.grpc.demo.StockQuote> stockQuoteIterator = blockingStub.serverSideStreamingGetListStockQuotes(Stock.newBuilder()
                .setTickerSymbol("AU").setCompanyName("Austich")
                .setDescription("server streaming example").build());

        while (stockQuoteIterator.hasNext()) {
            com.twang.grpc.demo.StockQuote stockQuote = stockQuoteIterator.next();
            System.out.println("stockQuote = " + stockQuote.toString());
        }
    }
}
