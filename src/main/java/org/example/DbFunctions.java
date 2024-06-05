package org.example;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DbFunctions {
    public Connection connectToDb() {
        Connection connection = null;
        try {
            InputStream inputStream = Main.class.getResourceAsStream("/config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);

            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + properties.getProperty("db_name"), properties.getProperty("db_user"), properties.getProperty("db_password"));
            if (connection != null) {
                System.out.println("DB Connection established");
            } else System.out.println("DB Connection failed");

        } catch (Exception e) {
            System.out.println(e);
        }
        return connection;
    }

    public boolean createTable(Connection connection) {
        Statement statement;
        try {
            String query = "CREATE TABLE IF NOT EXISTS " + "stock_transaction" + " (" +
                    "transaction_id SERIAL," +
                    "symbol VARCHAR(5)," +
                    "exchange VARCHAR(10)," +
                    "is_sold BOOLEAN," +
                    "price REAL," +
                    "shares REAL," +
                    "timestamp BIGINT," +
                    "primary key(transaction_id)" + ");";
            statement = connection.createStatement();
            statement.executeUpdate(query);
            System.out.println("Table Created");
            return true;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    private void insertRecord(Connection connection, StockValue stockValue, Boolean isSold, double totalPrice) {
        Statement statement;
        double stockShares;
        if(stockValue.getPrice()!=0)stockShares=(totalPrice / stockValue.getPrice());
        else stockShares=0;
        try {
            String query = "INSERT INTO stock_transaction (symbol, exchange, is_sold, price, shares, timestamp) VALUES(" +
                    "\'" + stockValue.getSymbol() + "\'," +
                    "\'" + stockValue.getExchange() + "\'," +
                    "\'" + isSold + "\'," +
                    "\'" + stockValue.getPrice() + "\'," +
                    "\'" + stockShares + "\'," +
                    "\'" + System.currentTimeMillis() / 1000 + "\')";
            statement = connection.createStatement();
            statement.executeUpdate(query);
            System.out.println("Record Inserted");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private double checkInvestmentTotal(Connection connection, String symbol) {
        Statement statement;
        try {
            String query = "SELECT " +
                    "(SUM(CASE WHEN is_sold = false THEN price * shares ELSE 0 END) - " +
                    "SUM(CASE WHEN is_sold = true THEN price * shares ELSE 0 END)) AS investment " +
                    "FROM stock_transaction WHERE symbol = '" + symbol + "';";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            return resultSet.getDouble("investment");
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }

    private double checkInvestmentsTotal(Connection connection) {
        Statement statement;
        try {
            String query = "SELECT " +
                    "SUM(CASE WHEN is_sold = false THEN price * shares ELSE 0 END) - " +
                    "SUM(CASE WHEN is_sold = true THEN price * shares ELSE 0 END) AS investment " +
                    "FROM stock_transaction;";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            return resultSet.getDouble("investment");
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }

    private double checkSharesTotal(Connection connection, String symbol) {
        Statement statement;
        try {
            String query = "SELECT " +
                    "(SUM(CASE WHEN is_sold = false THEN  shares ELSE 0 END) - " +
                    "SUM(CASE WHEN is_sold = true THEN  shares ELSE 0 END)) AS shares " +
                    "FROM stock_transaction WHERE symbol = '" + symbol + "';";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            return resultSet.getDouble("shares");
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }

    private double checkGainMoneyTotal(Connection connection, String symbol) {
        StockValue stockValue = new StockValue();
        stockValue.CheckStockValue(symbol);
        if (stockValue.checkStock() == stockInformation.full) {
            double investment = checkInvestmentTotal(connection, symbol);
            double shares = checkSharesTotal(connection, symbol);
            double priceNow = stockValue.getPrice();
            return (priceNow * shares) - investment;
        }
        return 0;
    }

    private double checkGainMoneyTotal(Connection connection, StockValue stockValue) {
        if (stockValue.checkStock() == stockInformation.full) {
            double investment = checkInvestmentTotal(connection, stockValue.getSymbol());
            double shares = checkSharesTotal(connection, stockValue.getSymbol());
            double priceNow = stockValue.getPrice();
            return (priceNow * shares) - investment;
        }
        return 0;
    }

    private double checkGainsMoneyTotal(Connection connection, List<StockValue> stockValuesArray) {
        double total=0;
        for (StockValue stockValue : stockValuesArray) {
                total += checkGainMoneyTotal(connection, stockValue);
            }
        return total;
    }

    private double checkGainRatioTotal(Connection connection, String symbol) {
        StockValue stockValue = new StockValue();
        stockValue.CheckStockValue(symbol);
        if (stockValue.checkStock() == stockInformation.full) {
            double investment = checkInvestmentTotal(connection, symbol);
            double shares = checkSharesTotal(connection, symbol);
            double priceNow = stockValue.getPrice();
            return (((priceNow * shares) / investment) - 1) * 100;
        }
        return 0;
    }
    private double checkGainRatioTotal(Connection connection, StockValue stockValue) {
        if (stockValue.checkStock() == stockInformation.full) {
            double investment = checkInvestmentTotal(connection, stockValue.getSymbol());
            double shares = checkSharesTotal(connection, stockValue.getSymbol());
            double priceNow = stockValue.getPrice();
            return (((priceNow * shares) / investment) - 1) * 100;
        }
        return 0;
    }

    public String addInvestment(Connection connection, String symbol, Boolean isSold, double totalPrice) {
        StockValue stockValue = new StockValue();
        stockValue.CheckStockValue(symbol);
        if (stockValue.checkStock() == stockInformation.full) {
            if ((isSold && checkInvestmentTotal(connection, stockValue.getSymbol()) - totalPrice >= 0) || !isSold) {
                insertRecord(connection, stockValue, isSold, totalPrice);
                return "Transaction completed";
            } else {
                return "Transaction Failed - no funds";
            }
        } else {
            return "Transaction Failed - no stock info";
        }
    }

    public String addInvestment(Connection connection, StockValue stockValue, Boolean isSold, double totalPrice) {
        if (stockValue.checkStock() == stockInformation.full) {
            if ((isSold && checkInvestmentTotal(connection, stockValue.getSymbol()) - totalPrice >= 0) || !isSold) {
                insertRecord(connection, stockValue, isSold, totalPrice);
                return "Transaction completed";
            } else {
                return "Transaction Failed - no funds";
            }
        } else {
            return "Transaction Failed - no stock info";
        }
    }

    public String checkInvestment(Connection connection, String symbol) {
        StockValue stockValue = new StockValue();
        stockValue.CheckStockValue(symbol);
        if (stockValue.checkStock() == stockInformation.full) {
            double investment = checkInvestmentTotal(connection, symbol);
            if (investment != 0) {
                return "Total invested in " + symbol + " = " + String.format("%.3f", investment) + " USD";
            } else return "No investments in " + symbol;
        } else return "No investments info - can't check investment total";
    }

    public String checkInvestment(Connection connection, StockValue stockValue) {

        if (stockValue.checkStock() == stockInformation.full) {
            double investment = checkInvestmentTotal(connection, stockValue.getSymbol());
            if (investment != 0) {
                return "Total invested in " + stockValue.getSymbol() + " = " + String.format("%.3f", investment) + " USD";
            } else return "No investments in " + stockValue.getSymbol();
        } else return "No investments info - can't check investment total";
    }

    public String checkInvestments(Connection connection) {
        double investment = checkInvestmentsTotal(connection);
        if (investment != 0) return "Total invested = " + String.format("%.3f", investment) + " USD";
        else return "No investments";
    }

    public String checkShares(Connection connection, String symbol) {
        StockValue stockValue = new StockValue();
        stockValue.CheckStockValue(symbol);
        if (stockValue.checkStock() == stockInformation.full) {
            double shares = checkSharesTotal(connection, symbol);
            if (shares != 0) {
                return "Total shares in " + symbol + " = " + String.format("%.3f", shares);
            } else return "No shares in " + symbol;
        } else return "No shares info - can't check total shares";
    }

    public String checkShares(Connection connection, StockValue stockValue) {
        if (stockValue.checkStock() == stockInformation.full) {
            double shares = checkSharesTotal(connection, stockValue.getSymbol());
            if (shares != 0) {
                return "Total shares in " + stockValue.getSymbol() + " = " + String.format("%.3f", shares);
            } else return "No shares in " + stockValue.getSymbol();
        } else return "No shares info - can't check total shares";
    }

    public String checkGainMoney(Connection connection, String symbol) {
        StockValue stockValue = new StockValue();
        stockValue.CheckStockValue(symbol);
        if (stockValue.checkStock() == stockInformation.full) {
            double money = checkGainMoneyTotal(connection, symbol);
            if (money != 0) {
                return "Total gain in " + symbol + " = " + String.format("%.3f", money) + " USD";
            } else return "No gain in " + symbol;
        } else return "No gain info - can't check investment total";
    }

    public String checkGainMoney(Connection connection, StockValue stockValue) {
        if (stockValue.checkStock() == stockInformation.full) {
            double money = checkGainMoneyTotal(connection, stockValue.getSymbol());
            if (money != 0) {
                return "Total gain in " + stockValue.getSymbol() + " = " + String.format("%.3f", money) + " USD";
            } else return "No gain in " + stockValue.getSymbol();
        } else return "No gain info - can't check investment total";
    }

    public String checkGainRatio(Connection connection, String symbol) {
        StockValue stockValue = new StockValue();
        stockValue.CheckStockValue(symbol);
        if (stockValue.checkStock() == stockInformation.full) {
            double ratio = checkGainRatioTotal(connection, symbol);
            if (ratio != 0) {
                return "Total gain ratio in " + symbol + " = " + String.format("%.3f", ratio) + " %";
            } else return "No gain ratio in " + symbol;
        } else return "No gain ratio info - can't check investment total";
    }

    public String checkGainRatio(Connection connection, StockValue stockValue) {
        if (stockValue.checkStock() == stockInformation.full) {
            double ratio = checkGainRatioTotal(connection, stockValue);
            if (ratio != 0) {
                return "Total gain ratio in " + stockValue.getSymbol() + " = " + String.format("%.3f", ratio) + " %";
            } else return "No gain ratio in " + stockValue.getSymbol();
        } else return "No gain ratio info - can't check investment total";
    }

    private List<StockTransaction> getTransactions(Connection connection) {
        Statement statement;
        try {
            String query = "SELECT * FROM stock_transaction ORDER BY timestamp DESC LIMIT 100;";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            List<StockTransaction> transactions = new ArrayList<>();
            while (resultSet.next()) {
                int transactionId = resultSet.getInt("transaction_id");
                String symbol = resultSet.getString("symbol");
                String exchange = resultSet.getString("exchange");
                boolean isSold = resultSet.getBoolean("is_sold");
                double price = resultSet.getDouble("price");
                double shares = resultSet.getDouble("shares");
                long timestamp = resultSet.getLong("timestamp");

                StockTransaction transaction = new StockTransaction(transactionId, symbol, exchange, isSold, price, shares, timestamp);
                transactions.add(transaction);
            }
            return transactions;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private List<StockValue> getStocks(Connection connection) {
        Statement statement;
        try {
            String query = "SELECT " +
                    "symbol, " +
                    "exchange, " +
                    "SUM(CASE WHEN is_sold = false THEN shares*price ELSE 0 END) - " +
                    "SUM(CASE WHEN is_sold = true THEN shares*price ELSE 0 END) AS investment " +
                    "FROM stock_transaction " +
                    "GROUP BY symbol, exchange " +
                    "ORDER BY symbol, exchange;";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            List<StockValue> transactions = new ArrayList<>();
            while (resultSet.next()) {
                String symbol = resultSet.getString("symbol");
                String exchange = resultSet.getString("exchange");
                double stocks = resultSet.getDouble("investment");

                StockValue transaction = new StockValue(symbol, "", stocks, exchange, 0);
                transactions.add(transaction);
            }
            return transactions;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public List<String> getStockSymbols(Connection connection) {
        Statement statement;
        try {
            String query = "SELECT " +
                    "symbol, " +
                    "exchange " +
                    "FROM stock_transaction " +
                    "GROUP BY symbol, exchange " +
                    "ORDER BY symbol, exchange;";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            List<String> stocks = new ArrayList<>();
            while (resultSet.next()) {
                String symbol = resultSet.getString("symbol");
                stocks.add(symbol);
            }
            return stocks;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public String printTransactions(Connection connection) {
        List<StockTransaction> transactionsArray = getTransactions(connection);
        String transactions = "";
        if (transactionsArray != null) {
            for (StockTransaction transaction : transactionsArray) {
                transactions += transaction + System.lineSeparator();
            }
            return transactions;
        } else return "No transaction history";
    }

    public String printStocks(Connection connection) {
        List<String> symbols = getStockSymbols(connection);
        StockValue stockValue = new StockValue();
        List<StockValue> stockArray = stockValue.CheckStockValues(symbols);
        String transactions = "";
        if (stockArray != null) {
            for (StockValue stock : stockArray) {
                transactions += stock.getExchange() + ": " + stock.getSymbol() + " =   " + String.format("%.3f",checkGainRatioTotal(connection, stock)) + " %" + System.lineSeparator();
            }
            return transactions;
        } else return "No transaction history";
    }
    public String printStocks(Connection connection, List<StockValue> stockArray) {
        String transactions = "";
        if (stockArray != null) {
            for (StockValue stock : stockArray) {
                transactions += stock.getExchange() + ": " + stock.getSymbol() + " =   " + String.format("%.3f",checkGainRatioTotal(connection, stock)) + " %" + System.lineSeparator();
            }
            return transactions;
        } else return "No transaction history";
    }

    public String printGain(Connection connection){
        StockValue stockValue = new StockValue();
        return "Total stocks gain: " +  String.format("%.3f",checkGainsMoneyTotal(connection, stockValue.CheckStockValues(getStockSymbols(connection)))) + " USD";
    }
    public String printGain(Connection connection, List<StockValue> stockArray){
        StockValue stockValue = new StockValue();
        return "Total stocks gain: " +  String.format("%.3f",checkGainsMoneyTotal(connection, stockArray)) + " USD";
    }
}