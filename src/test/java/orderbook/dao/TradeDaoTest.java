package orderbook.dao;

import orderbook.dto.Trade;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import orderbook.exceptions.PersistenceException;

import static org.junit.jupiter.api.Assertions.*;

class TradeDaoTest {

    public static final String TRADE_FILE = "tradesTest.txt";
    private Map<Integer,Trade> trades=new HashMap<>();
    private TradeDao tradeDao=new TradeDaoFileImpl();


    @Test
    void logTrade() {
        PrintWriter out = null;
        Trade trade=new Trade(40,new BigDecimal("20"));

        try {
            out = new PrintWriter(new FileWriter(TRADE_FILE, true));
        } catch (IOException e) {
            System.out.println("Could not persist trade information.");
        }


        out.println(trade);
        out.flush();
        assertNotNull(TRADE_FILE,"Should not be null");
    }

    @Test
    void getTrade() throws PersistenceException {
        Trade trade=new Trade(44,new BigDecimal("30"));
        trades.put(trade.getTradeId(),trade);
        Trade  newTrade= tradeDao.getTrade(trade.getTradeId());

        assertNotNull(newTrade,"Should not be null");

    }

    @Test
    void getTrades() {
        addTrade();
     List<Trade> tradeObj=new ArrayList<>(trades.values());

     assertFalse(tradeObj.isEmpty(),"Should not be empty");
     assertEquals(tradeObj.size(),2,"Should be two");

    }

    @Test
    void addTrade() {
        Trade trade=new Trade(44,new BigDecimal("30"));
        Trade trade2=new Trade(34,new BigDecimal("20"));
        trades.put(trade.getTradeId(),trade);
        trades.put(trade2.getTradeId(),trade2);

        assertFalse(trades.isEmpty(),"Should not be empty");
        assertEquals(trades.size(),2,"Should be two");
    }
}