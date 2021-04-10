package orderbook.dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import orderbook.dto.Trade;
import orderbook.exceptions.PersistenceException;

public class TradeDaoFileImpl implements TradeDao {

    public static final String TRADE_FILE = "trades.txt";  //why public static?
    private final String DELIMITER = "::";

   private Map<Integer,Trade> trades = new HashMap<>();

    @Override
    public void addTrade(Trade trade) throws PersistenceException {
        trades.put(trade.getTradeId(),trade);
        logTrade(trade);
    }
    
    @Override
    public Trade getTrade(int id) throws PersistenceException {
        loadTrades();
        Trade returnTrade=null;
        for (Trade trade:trades.values()) {
                if (trade.getTradeId()==id)
                    returnTrade=trade;
        }
        return returnTrade;
    }

    @Override
    public List<Trade> getTrades() throws PersistenceException {
        loadTrades();
        return new ArrayList<>(trades.values());
    }
    
    public int getLastTradeId() throws PersistenceException {
        loadTrades();
        return trades.values().stream()
                              .map(t -> t.getTradeId())
                              .reduce(0, Integer::max);
    }
   
    @Override
    public void logTrade(Trade trade) throws PersistenceException {
        PrintWriter out = null;

        try {
            out = new PrintWriter(new FileWriter(TRADE_FILE, true));
        } catch (IOException e) {
            throw new PersistenceException(
            "Could not save sell order data",e);
        }

        out.println(marshallTrade(trade));
        out.flush();
    }
    
    private String marshallTrade(Trade trade) {
        String tradeAsText = trade.getTradeId() + DELIMITER;
        tradeAsText += trade.getExecutionTime().toString() + DELIMITER;
        tradeAsText += trade.getQuantity() + DELIMITER;
        tradeAsText += trade.getPrice().toString();
        return tradeAsText;
    }
    
     private Trade unmarshallTrade(String tradeAsText) {
        String [] tradeTokens = tradeAsText.split(DELIMITER);
        Trade tradeFromFile = new Trade();
        tradeFromFile.setTradeId(Integer.parseInt(tradeTokens[0]));
        tradeFromFile.setExecutionTime(LocalDateTime.parse(tradeTokens[1]));  
        tradeFromFile.setQuantity(Integer.parseInt(tradeTokens[2]));
        tradeFromFile.setPrice(new BigDecimal(tradeTokens[3]));
        return tradeFromFile;
     }
    
    private void loadTrades() throws PersistenceException {
        Scanner scanner = null;
        try {
            scanner = new Scanner(
                new BufferedReader(
                    new FileReader(TRADE_FILE)));
        } catch (FileNotFoundException e) {
            throw new PersistenceException(
            "Could not load trade data into memory",e);
        }
        
        //Read from file
        String currentLine; //holds the most recent line read from the file
        Trade currentTrade;  //holds the most recent unmarshalled order

        while (scanner.hasNextLine()) {
            //get the next line in the file
            currentLine = scanner.nextLine();

            //unmarshall the line into a trade
            currentTrade = unmarshallTrade(currentLine);
            
            trades.put(currentTrade.getTradeId(),currentTrade);
        }
        //Clean up/close file
        scanner.close();
    }
    
}
