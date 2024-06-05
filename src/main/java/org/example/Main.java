package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        DbFunctions db=new DbFunctions();
        Connection connection =db.connectToDb();
        db.createTable(connection);

        StockValue stockValue = new StockValue();
        //stockValue.CheckStockValue("SPCE");

        StockMarket stockMarket = new StockMarket();
        //stockMarket.isMarketOpened("NASDAQ");

        //System.out.println(stockValue);

        //System.out.println(db.addInvestment(connection,"SPCE",true, 100));
        //System.out.println(db.checkInvestment(connection,"SPCE"));
        //System.out.println(db.checkShares(connection,"SPCE"));
        //System.out.println(db.checkInvestments(connection));
        //System.out.println(db.checkGainMoney(connection,"SPCE"));
        //System.out.println(db.checkGainRatio(connection,"SPCE"));

        //System.out.println(db.addInvestment(connection,stockValue,true, 100));
        //System.out.println(db.checkInvestment(connection,stockValue));
        //System.out.println(db.checkShares(connection,stockValue));
        //System.out.println(db.checkInvestments(connection));
        //System.out.println(db.checkGainMoney(connection,stockValue));
        //System.out.println(db.checkGainRatio(connection,stockValue));
        List<String> arr = new ArrayList<>();
        arr.add("SPCE");
        arr.add("AAPL");
        List<StockValue> stockValuesArr;
        stockValuesArr = stockValue.CheckStockValues(arr);
        for (StockValue stockValueA : stockValuesArr) {
            //System.out.println(stockValueA.toString());
        }
        System.out.println(db.printGain(connection));
        
    }
}