package orderbook.dao;

import orderbook.dto.Trade;

import java.util.List;
import orderbook.exceptions.PersistenceException;

public interface TradeDao {

   void logTrade(Trade trade) throws PersistenceException;
   
   Trade getTrade (int id) throws PersistenceException;
   
   List<Trade> getTrades() throws PersistenceException;
   
   void addTrade(Trade trade)throws PersistenceException;
   
   int getLastTradeId() throws PersistenceException;
}
