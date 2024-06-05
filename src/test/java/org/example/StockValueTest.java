package org.example;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.example.StockValue.getJsonFromUrl;
import static org.junit.jupiter.api.Assertions.*;

class StockValueTest {

    @Test
    public void PobieranieDanychAPI() {
        StockValue stockValue = new StockValue();
        stockValue.CheckStockValue("SPCE");

        assert stockValue != null;
        assertSame(stockValue.checkStock(), stockInformation.full);
    }

}