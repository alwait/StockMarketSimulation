package org.example;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

enum stockInformation {full, symbolOnly, noInfo}
public class StockValue {
    private String symbol;
    private String name;
    private double price;
    private String exchange;
    private long timestamp;

    public StockValue() {

    }

    public String getSymbol(){
        return symbol;
    }
    public String getName(){
        return name;
    }
    public double getPrice(){
        return price;
    }
    public String getExchange(){
        return exchange;
    }
    public long getTimestamp(){
        return timestamp;
    }
    public StockValue(String symbol, String name, double price, String exchange, long timestamp){
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.exchange = exchange;
        this.timestamp = timestamp;
    }
    public void clearStockValue(){
        this.symbol = null;
        this.name = null;
        this.price = 0;
        this.exchange = null;
        this.timestamp = 0;
    }

    public void CheckStockValue(String symbol){
        String configFile = "config.properties";
        try {
            InputStream inputStream = Main.class.getResourceAsStream("/config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            String url = "https://financialmodelingprep.com/api/v3/quote/" + symbol + "?apikey=" + properties.getProperty("api_key");
            String json = getJsonFromUrl(url);
            Gson gson = new Gson();
            StockValue[] stockValues = gson.fromJson(json, StockValue[].class);
            System.out.println("API Call");
            if(stockValues.length>0) {
                this.symbol = stockValues[0].symbol;
                this.name = stockValues[0].name;
                this.price = stockValues[0].price;
                this.exchange = stockValues[0].exchange;
                this.timestamp = stockValues[0].timestamp;
            } else this.symbol = symbol;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<StockValue> CheckStockValues(List<String> symbol){
        String configFile = "config.properties";
        try {
            InputStream inputStream = Main.class.getResourceAsStream("/config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            String symbols="";
            for (int i=0; i<symbol.size();++i) {
                symbols+=symbol.get(i).toString();
                if (i<symbol.size()-1) {
                    symbols+=",";
                }
            }
            String url = "https://financialmodelingprep.com/api/v3/quote/" + symbols.toString() + "?apikey=" + properties.getProperty("api_key");
            String json = getJsonFromUrl(url);
            Gson gson = new Gson();
            StockValue[] stockValues = gson.fromJson(json, StockValue[].class);
            System.out.println("API Call");
            List<StockValue> stockValuesArr = new ArrayList<>();
            for (StockValue stockValue : stockValues) {
                stockValuesArr.add(stockValue);
            }
            return stockValuesArr;

        } catch (IOException e) {

            e.printStackTrace();
        }
       return null;
    }
    static String getJsonFromUrl(String urlString) throws IOException {

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        reader.close();
        connection.disconnect();

        return stringBuilder.toString();
    }

    public LocalDateTime ConvertDate(long timestamp){
        Instant instant = Instant.ofEpochSecond(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public String ConvertDateToString(long timestamp){
        LocalDateTime dateTime = ConvertDate(timestamp);
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        return formattedDateTime;
    }

    public stockInformation checkStock(){
        if(this.symbol!=null && this.exchange!=null) return stockInformation.full;
        else if(symbol!=null) return stockInformation.symbolOnly;
        else return stockInformation.noInfo;
    }

    @Override
    public String toString(){
        stockInformation stockInfo = this.checkStock();
        if(stockInfo==stockInformation.full) {
            return exchange + ": " +
                    symbol + System.lineSeparator() +
                    name + System.lineSeparator() +
                    "Price: " + price + " USD" + System.lineSeparator() +
                    "Updated on " + ConvertDateToString(timestamp);
        }
        else if(stockInfo==stockInformation.symbolOnly) return "No stock information about: " + symbol;
        else return "No stock information given - can't print info";
    }
}
