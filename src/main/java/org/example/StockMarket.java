package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.example.StockValue.getJsonFromUrl;

public class StockMarket {
    Boolean isTheStockMarketOpen;

    public void isMarketOpened(String exchange){
        String configFile = "config.properties";
        try {
            InputStream inputStream = Main.class.getResourceAsStream("/config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            String url = "https://financialmodelingprep.com/api/v3/is-the-market-open?exchange=" + exchange + "&apikey=" + properties.getProperty("api_key");
            String json = getJsonFromUrl(url);
            Gson gson = new Gson();
            StockMarket stockMarket = gson.fromJson(json, StockMarket.class);
            if(stockMarket!=null) {
                this.isTheStockMarketOpen = stockMarket.isTheStockMarketOpen;
            } else this.isTheStockMarketOpen = null;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
