package orderbook.dao;

import java.util.List;
import orderbook.dto.Order;
import orderbook.dto.SellOrder;
import orderbook.exceptions.PersistenceException;

public interface SellOrderDao {
    
    boolean addSellOrder(SellOrder sellOrder) throws PersistenceException;
    
    List<Order> getAllSellOrders() throws PersistenceException;
    
    void editQuantitySellOrder(Order sellOrder, int quantity) throws PersistenceException;
    
    void removeSellOrder(Order sellOrder) throws PersistenceException;
    
    int getLastSellOrderId() throws PersistenceException;
    
}
