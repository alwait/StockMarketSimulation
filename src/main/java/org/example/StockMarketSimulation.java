package org.example;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StockMarketSimulation {

    private JPanel panel1;
    private JButton buttonRefresh;
    private JTextArea textGainInfo;
    private JTextArea textGain;
    private JTextArea textDateTimeRefresh;
    private JButton buttonCheckStock;
    private JTextField inputSymbol;
    private JTextArea textStockValue;
    private JTextArea textGainStock;
    private JButton buttonBuy;
    private JButton buttonSell;
    private JTextField textMoneyAmount;
    private JTextPane textSharesInfo;
    private JTextPane textTransaction;
    private JTextPane textHistory;
    private JLabel title;
    private JTextPane textStocks;
    private JPanel panelHistory;
    private JPanel panelStocks;
    private JScrollPane scrollHistory;
    private JScrollPane scrollStocks;


    public StockMarketSimulation() {
        DbFunctions db = new DbFunctions();
        Connection connection = db.connectToDb();
        db.createTable(connection);
        scrollHistory = new JScrollPane(textHistory);
        panelHistory.add(scrollHistory);
        scrollStocks = new JScrollPane(textStocks);
        panelStocks.add(scrollStocks);
        textStockValue.setText("(Check stock for informations)");
        textSharesInfo.setText("(No stock chosen)");
        StockValue stockValue = new StockValue();
        StockValue stockValueBase = new StockValue();
        List<StockValue> stockArray = stockValue.CheckStockValues(db.getStockSymbols(connection));
        updateDateTime();
        updateInvestmentsInfo(db, connection);
        updateHistory(db, connection);
        updateStocks(db, connection, stockArray);
        updateGain(db, connection, stockArray);

        buttonRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDateTime();
                updateInvestmentsInfo(db, connection);
                updateHistory(db, connection);
                updateStocks(db, connection, stockArray);
                updateGain(db, connection, stockArray);
                List<StockValue> stockArray = stockValue.CheckStockValues(db.getStockSymbols(connection));
            }
        });
        buttonCheckStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String symbol = inputSymbol.getText();
                if (symbol != "") {
                    stockValue.clearStockValue();
                    stockValue.CheckStockValue(symbol);
                    if (stockValue.checkStock() == stockInformation.full) {
                        updateDateTime();
                        updateStockInfo(stockValue);
                        updateStockGain(db, connection, stockValue);
                        updateSharesInfo(db, connection, stockValue);
                    } else {
                        textStockValue.setText("(No stock information)");
                        textSharesInfo.setText("(No stock chosen)");
                        textGainStock.setText("");
                    }
                } else {
                    textStockValue.setText("(Check stock for informations)");
                    textSharesInfo.setText("(No stock chosen)");
                    textGainStock.setText("");
                }
            }
        });
        buttonBuy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String money = textMoneyAmount.getText();
                if (money != "") {
                    try {
                        double moneyAmount = Double.parseDouble(money);
                        textTransaction.setText(db.addInvestment(connection, stockValue, false, moneyAmount));
                    } catch (NumberFormatException n) {
                        textTransaction.setText("Transaction error");
                    }
                    updateInvestmentsInfo(db, connection);
                    updateStockGain(db, connection, stockValue);
                    updateSharesInfo(db, connection, stockValue);
                    updateHistory(db, connection);
                    updateStocks(db, connection, stockArray);
                }
            }
        });
        buttonSell.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String money = textMoneyAmount.getText();
                if (money != "") {
                    try {
                        double moneyAmount = Double.parseDouble(money);
                        textTransaction.setText(db.addInvestment(connection, stockValue, true, moneyAmount));
                    } catch (NumberFormatException n) {
                        textTransaction.setText("Transaction error");
                    }
                    updateInvestmentsInfo(db, connection);
                    updateStockGain(db, connection, stockValue);
                    updateSharesInfo(db, connection, stockValue);
                    updateHistory(db, connection);
                    updateStocks(db, connection, stockArray);
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("StockMarketSimulation");
        frame.setContentPane(new StockMarketSimulation().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.setVisible(true);
    }

    public void updateDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        Date date = new Date();
        textDateTimeRefresh.setText(formatter.format(date));
    }

    public void updateInvestmentsInfo(DbFunctions db, Connection connection) {
        textGainInfo.setText(db.checkInvestments(connection));
    }

    public void updateStockInfo(StockValue stockValue) {
        textStockValue.setText(stockValue.toString());
    }

    public void updateSharesInfo(DbFunctions db, Connection connection, StockValue stockValue) {
        textSharesInfo.setText(db.checkShares(connection, stockValue) + System.lineSeparator() + db.checkInvestment(connection, stockValue));
    }

    public void updateStockGain(DbFunctions db, Connection connection, StockValue stockValue) {
        textGainStock.setText(db.checkGainMoney(connection, stockValue) + System.lineSeparator() + db.checkGainRatio(connection, stockValue));

    }

    public void updateHistory(DbFunctions db, Connection connection) {
        textHistory.setText(db.printTransactions(connection));
    }

    public void updateStocks(DbFunctions db, Connection connection, List<StockValue> stockArray) {
        textStocks.setText(db.printStocks(connection, stockArray));
    }

    public void updateGain(DbFunctions db, Connection connection, List<StockValue> stockArray) {
        textGain.setText(db.printGain(connection, stockArray));
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        panel1.setBackground(new Color(-59087));
        panel1.setForeground(new Color(-1));
        panel1.setPreferredSize(new Dimension(700, 400));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel2.setPreferredSize(new Dimension(160, 400));
        panel1.add(panel2, BorderLayout.WEST);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        panel3.setPreferredSize(new Dimension(0, 100));
        panel2.add(panel3, BorderLayout.SOUTH);
        final JLabel label1 = new JLabel();
        label1.setRequestFocusEnabled(false);
        label1.setText("Refresh stock data");
        panel3.add(label1, BorderLayout.NORTH);
        buttonRefresh = new JButton();
        buttonRefresh.setText("Refresh");
        panel3.add(buttonRefresh, BorderLayout.CENTER);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        panel4.setPreferredSize(new Dimension(0, 40));
        panel3.add(panel4, BorderLayout.SOUTH);
        final JLabel label2 = new JLabel();
        label2.setText("Last data refresh:");
        panel4.add(label2, BorderLayout.NORTH);
        textDateTimeRefresh = new JTextArea();
        textDateTimeRefresh.setBackground(new Color(-1184018));
        textDateTimeRefresh.setText("");
        panel4.add(textDateTimeRefresh, BorderLayout.CENTER);
        title = new JLabel();
        title.setAlignmentX(0.0f);
        title.setAlignmentY(0.0f);
        title.setAutoscrolls(false);
        title.setBackground(new Color(-3951153));
        Font titleFont = this.$$$getFont$$$(null, -1, 12, title.getFont());
        if (titleFont != null) title.setFont(titleFont);
        title.setPreferredSize(new Dimension(160, 40));
        title.setText("Stock Market Simulation");
        panel2.add(title, BorderLayout.NORTH);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 0));
        panel2.add(panel5, BorderLayout.CENTER);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout(0, 0));
        panel6.setPreferredSize(new Dimension(0, 80));
        panel5.add(panel6, BorderLayout.NORTH);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new BorderLayout(0, 0));
        panel5.add(panel7, BorderLayout.CENTER);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new BorderLayout(0, 0));
        panel8.setPreferredSize(new Dimension(0, 80));
        panel7.add(panel8, BorderLayout.NORTH);
        final JLabel label3 = new JLabel();
        label3.setText("Money invested info:");
        panel8.add(label3, BorderLayout.NORTH);
        textGainInfo = new JTextArea();
        textGainInfo.setAutoscrolls(true);
        textGainInfo.setBackground(new Color(-1184018));
        textGainInfo.setCaretColor(new Color(-3092015));
        textGainInfo.setEditable(false);
        textGainInfo.setForeground(new Color(-16777216));
        textGainInfo.setMargin(new Insets(0, 0, 0, 0));
        panel8.add(textGainInfo, BorderLayout.CENTER);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new BorderLayout(0, 0));
        panel9.setPreferredSize(new Dimension(0, 80));
        panel7.add(panel9, BorderLayout.SOUTH);
        final JLabel label4 = new JLabel();
        label4.setText("Investments gains:");
        panel9.add(label4, BorderLayout.NORTH);
        textGain = new JTextArea();
        textGain.setBackground(new Color(-1184018));
        textGain.setEditable(false);
        panel9.add(textGain, BorderLayout.CENTER);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new BorderLayout(0, 0));
        panel10.setPreferredSize(new Dimension(0, 80));
        panel7.add(panel10, BorderLayout.CENTER);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new BorderLayout(0, 0));
        panel1.add(panel11, BorderLayout.CENTER);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new BorderLayout(0, 0));
        panel12.setPreferredSize(new Dimension(0, 60));
        panel11.add(panel12, BorderLayout.NORTH);
        final JLabel label5 = new JLabel();
        label5.setText("Check stock by symbol input:                              Invested stocks: ");
        panel12.add(label5, BorderLayout.NORTH);
        buttonCheckStock = new JButton();
        buttonCheckStock.setPreferredSize(new Dimension(180, 30));
        buttonCheckStock.setText("Check stock");
        panel12.add(buttonCheckStock, BorderLayout.WEST);
        panelStocks = new JPanel();
        panelStocks.setLayout(new BorderLayout(0, 0));
        panelStocks.setForeground(new Color(-16777216));
        panel12.add(panelStocks, BorderLayout.CENTER);
        inputSymbol = new JTextField();
        inputSymbol.setCaretColor(new Color(-7961468));
        Font inputSymbolFont = this.$$$getFont$$$(null, -1, 20, inputSymbol.getFont());
        if (inputSymbolFont != null) inputSymbol.setFont(inputSymbolFont);
        inputSymbol.setPreferredSize(new Dimension(80, 30));
        inputSymbol.setSelectedTextColor(new Color(-6381662));
        inputSymbol.setToolTipText("Symbol");
        panelStocks.add(inputSymbol, BorderLayout.WEST);
        textStocks = new JTextPane();
        textStocks.setAutoscrolls(true);
        textStocks.setBackground(new Color(-1184018));
        textStocks.setEditable(false);
        textStocks.setPreferredSize(new Dimension(7, 80));
        panelStocks.add(textStocks, BorderLayout.CENTER);
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new BorderLayout(0, 0));
        panel11.add(panel13, BorderLayout.CENTER);
        final JLabel label6 = new JLabel();
        label6.setText("Stock info:                                            Invest money:");
        panel13.add(label6, BorderLayout.NORTH);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new BorderLayout(0, 0));
        panel14.setPreferredSize(new Dimension(200, 0));
        panel13.add(panel14, BorderLayout.WEST);
        textStockValue = new JTextArea();
        textStockValue.setBackground(new Color(-1184018));
        textStockValue.setEditable(false);
        textStockValue.setPreferredSize(new Dimension(1, 80));
        panel14.add(textStockValue, BorderLayout.NORTH);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new BorderLayout(0, 0));
        panel14.add(panel15, BorderLayout.CENTER);
        textGainStock = new JTextArea();
        textGainStock.setAlignmentX(0.5f);
        textGainStock.setBackground(new Color(-1184018));
        textGainStock.setEditable(false);
        textGainStock.setPreferredSize(new Dimension(1, 40));
        panel15.add(textGainStock, BorderLayout.NORTH);
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new BorderLayout(0, 0));
        panel13.add(panel16, BorderLayout.CENTER);
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new BorderLayout(0, 0));
        panel17.setPreferredSize(new Dimension(0, 120));
        panel16.add(panel17, BorderLayout.NORTH);
        final JLabel label7 = new JLabel();
        label7.setText("");
        panel17.add(label7, BorderLayout.NORTH);
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new BorderLayout(0, 0));
        panel18.setPreferredSize(new Dimension(49, 53));
        panel17.add(panel18, BorderLayout.CENTER);
        textSharesInfo = new JTextPane();
        textSharesInfo.setBackground(new Color(-1184018));
        textSharesInfo.setEditable(false);
        panel18.add(textSharesInfo, BorderLayout.CENTER);
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new BorderLayout(0, 0));
        panel19.setPreferredSize(new Dimension(0, 20));
        panel18.add(panel19, BorderLayout.NORTH);
        textMoneyAmount = new JTextField();
        textMoneyAmount.setPreferredSize(new Dimension(80, 30));
        panel19.add(textMoneyAmount, BorderLayout.WEST);
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new BorderLayout(0, 0));
        panel20.setPreferredSize(new Dimension(100, 0));
        panel17.add(panel20, BorderLayout.EAST);
        buttonBuy = new JButton();
        buttonBuy.setPreferredSize(new Dimension(78, 50));
        buttonBuy.setText("Buy");
        panel20.add(buttonBuy, BorderLayout.NORTH);
        buttonSell = new JButton();
        buttonSell.setPreferredSize(new Dimension(78, 50));
        buttonSell.setText("Sell");
        panel20.add(buttonSell, BorderLayout.SOUTH);
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new BorderLayout(0, 0));
        panel21.setPreferredSize(new Dimension(0, 80));
        panel16.add(panel21, BorderLayout.CENTER);
        textTransaction = new JTextPane();
        textTransaction.setBackground(new Color(-1184018));
        textTransaction.setEditable(false);
        Font textTransactionFont = this.$$$getFont$$$(null, Font.ITALIC, -1, textTransaction.getFont());
        if (textTransactionFont != null) textTransaction.setFont(textTransactionFont);
        textTransaction.setPreferredSize(new Dimension(7, 30));
        panel21.add(textTransaction, BorderLayout.NORTH);
        panelHistory = new JPanel();
        panelHistory.setLayout(new BorderLayout(0, 0));
        panelHistory.setPreferredSize(new Dimension(0, 200));
        panel13.add(panelHistory, BorderLayout.SOUTH);
        final JLabel label8 = new JLabel();
        label8.setText("Transaction history");
        panelHistory.add(label8, BorderLayout.NORTH);
        textHistory = new JTextPane();
        textHistory.setAutoscrolls(true);
        textHistory.setBackground(new Color(-1184018));
        textHistory.setEditable(false);
        textHistory.setMinimumSize(new Dimension(100, 100));
        textHistory.setPreferredSize(new Dimension(400, 400));
        textHistory.setText("");
        panelHistory.add(textHistory, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
