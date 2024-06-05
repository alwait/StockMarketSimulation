package org.example;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StockTransaction {
    private int transactionId;
    private String symbol;
    private String exchange;
    private boolean isSold;
    private double price;
    private double shares;
    private long timestamp;
    public StockTransaction(int transactionId, String symbol, String exchange, boolean isSold, double price, double shares, long timestamp) {
        this.transactionId = transactionId;
        this.symbol = symbol;
        this.exchange = exchange;
        this.isSold = isSold;
        this.price = price;
        this.shares = shares;
        this.timestamp = timestamp;
    }

    @Override
    public String toString(){
        String sold = "BUY";
        if(isSold) sold = "SELL";
        StockValue stockValue = new StockValue();
        return  sold + " " + String.format("%.3f", price*shares) + " USD - " + exchange + ": " + symbol + "       " + stockValue.ConvertDateToString(timestamp);
    }
}
