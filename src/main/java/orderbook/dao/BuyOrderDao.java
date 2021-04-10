package orderbook.dao;

import java.util.List;
import orderbook.dto.BuyOrder;
import orderbook.dto.Order;
import orderbook.exceptions.PersistenceException;

public interface BuyOrderDao {
    
    boolean addBuyOrder(BuyOrder buyOrder)throws PersistenceException;
    
    List<Order> getAllBuyOrders()throws PersistenceException;
    
    void removeBuyOrder(Order buyOrder)throws PersistenceException;
    
    int getLastBuyOrderId() throws PersistenceException;
            
}
