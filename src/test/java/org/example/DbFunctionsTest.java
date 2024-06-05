package org.example;

import jdk.jfr.Timestamp;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DbFunctionsTest {

    @Test
    public void TestPolaczeniaZBaza(){
        DbFunctions db = new DbFunctions();
        Connection connnection = db.connectToDb();
        assertNotNull(connnection);
    }

    @Test
    public void DodanieTabeliDoBazy(){
        DbFunctions db = new DbFunctions();
        assertTrue(db.createTable(db.connectToDb()));
    }

    @Test
    public void DodanieRekorduDoBazy(){
        DbFunctions db = new DbFunctions();
        StockValue stockValue = new StockValue("TEST","Unit Test stock",0,"UNIT", 0);

        assertEquals("Transaction completed",db.addInvestment(db.connectToDb(),stockValue,false,0));
    }

}