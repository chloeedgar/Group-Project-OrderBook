
package orderbook.service;

import java.math.BigDecimal;
import java.util.List;
import orderbook.dto.BuyOrder;
import orderbook.dto.Order;
import orderbook.dto.SellOrder;
import orderbook.dto.Trade;
import orderbook.exceptions.NoMatchException;
import orderbook.exceptions.PersistenceException;

public interface OrderBookServiceLayer {

    List<List<Order>> getOrderBook() throws PersistenceException;  
    
    boolean checkOrderEmpty()throws PersistenceException;
    
    List<BigDecimal> getStats(List<List<Order>> orderBook) throws PersistenceException;
    
    Trade match() throws NoMatchException, PersistenceException ;
    
    int matchAllOrders() throws NoMatchException, PersistenceException;
    
    BuyOrder addBuyOrder(BuyOrder buyOrder) throws PersistenceException;
    
    SellOrder addSellOrder(SellOrder sellOrder) throws PersistenceException;

    Trade getTrade (int id)throws PersistenceException;

    List<Trade> getTrades()throws PersistenceException;
    
    void updateGlobalOrderId() throws PersistenceException;
    
    void updateGlobalTradeId() throws PersistenceException;
    
    void generateOrders(int amount) throws PersistenceException;
}
