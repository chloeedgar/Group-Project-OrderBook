
package orderbook.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import orderbook.dao.BuyOrderDao;
import orderbook.dao.SellOrderDao;
import orderbook.dao.TradeDao;
import orderbook.dto.BuyOrder;
import orderbook.dto.Order;
import orderbook.dto.SellOrder;
import orderbook.dto.Trade;
import orderbook.exceptions.NoMatchException;
import orderbook.exceptions.PersistenceException;

public class OrderBookServiceLayerImpl implements OrderBookServiceLayer {
    
    private BuyOrderDao buyOrderDao;
    private SellOrderDao sellOrderDao;
    private TradeDao tradeDao;


    public OrderBookServiceLayerImpl(BuyOrderDao buyOrderDao, SellOrderDao sellOrderDao, TradeDao tradeDao) {
        this.buyOrderDao = buyOrderDao;
        this.sellOrderDao = sellOrderDao;
        this.tradeDao = tradeDao;
    }

    /**
     * Collects the lists of SellOrders and BuyOrders into an order book.
     * @return a List object containing first the buyOrders list and second the sellOrders list.
     */
    @Override
    public List<List<Order>> getOrderBook() throws PersistenceException {
        List<List<Order>> orderBook = new ArrayList<>();
        
        orderBook.add(buyOrderDao.getAllBuyOrders());
        orderBook.add(sellOrderDao.getAllSellOrders());
        
        return orderBook;
    }
    
    /**
     * Returns whether either the order book list of BuyOrders OR SellOrders is empty.
     * @return 
     */
    @Override
    public boolean checkOrderEmpty() throws PersistenceException {
        return buyOrderDao.getAllBuyOrders().isEmpty() || sellOrderDao.getAllSellOrders().isEmpty();
    }

    /**
     * Returns a list of statistics on: the number of BuyOrders; the number of SellOrders; the total BuyOrder
     * quantity; the total SellOrder quantity; the average BuyOrder price; and the average SellOrder price from
     * the order book. 
     * @param orderBook a List object containing first the buyOrders list and second the sellOrders list.
     * @return a BigDecimal list of the various order book statistics.
     */
    @Override
    public List<BigDecimal> getStats(List<List<Order>> orderBook) {
        
        List<BigDecimal> stats = new ArrayList<>();        
        
        stats.add(BigDecimal.valueOf(orderBook.get(0).size()));     // number of buyOrders
        stats.add(BigDecimal.valueOf(orderBook.get(1).size())); 
        //stats.add(BigDecimal.ZERO);     // number of sellOrders
        
        // total quantity of buyOrders
        stats.add(new BigDecimal( orderBook.get(0).stream()     
                                                  .map(o -> o.getQuantity())
                                                  .reduce(0, Integer::sum)
        ));
        
        // total quantity of sellOrders
        stats.add(new BigDecimal( orderBook.get(1).stream()     
                                                  .map(o -> o.getQuantity())
                                                  .reduce(0, Integer::sum)
        ));
        
        // Ensures no division by zero.
        if (stats.get(0).compareTo(BigDecimal.ZERO) != 0) {
            // average BuyOrder price
            stats.add( (orderBook.get(0).stream()     
                                        .map(o -> o.getPrice())
                                        .reduce(BigDecimal.ZERO, BigDecimal::add) // Sums all prices 
            ).divide(stats.get(0), 5, RoundingMode.HALF_UP));                     // Divides by stats[0], i.e. number of buyOrders
        }
        else {
            stats.add(BigDecimal.ZERO);
        }
        
        // Ensures no division by zero.
        if (stats.get(1).compareTo(BigDecimal.ZERO) != 0) {
            // average SellOrder price
            stats.add( (orderBook.get(1).stream()     
                                        .map(o -> o.getPrice())
                                        .reduce(BigDecimal.ZERO, BigDecimal::add) // Sums all prices 
            ).divide(stats.get(1), 5, RoundingMode.HALF_UP));                     // Divides by stats[1], i.e. number of sellOrders
        }
        else {
            stats.add(BigDecimal.ZERO);
        }
        
        return stats;
    }

    /**
     * Retrieves the bestBid, the highest buy order, and the bestAsk, the lowest sell order, from the sorted 
     * buyOrders/sellOrders list respectively. A Trade object is then created and stored, and the logic for
     * the full or partial fulfilment of orders is then initiated.
     * @return the Trade object resulting from the match.
     */
    @Override
    public Trade match() throws NoMatchException, PersistenceException {
        updateGlobalTradeId();
        
        Comparator<Order> compareByPrice = (Order o1, Order o2) -> o1.getPrice().compareTo(o2.getPrice());
        
        // Gets the sellOrders and sorts by lowest price first
        List<Order> sellOrders = sellOrderDao.getAllSellOrders();
        sellOrders.sort(compareByPrice);
        
        // Gets the buy order with the highest price and sell order with the lowest price
        Order bestBid = buyOrderDao.getAllBuyOrders().get(0);
        Order bestAsk = sellOrders.get(0);
        
        if ((bestBid.getPrice()).compareTo(bestAsk.getPrice()) < 0) {
            throw new NoMatchException("The best bid price is not sufficient to fulfill the best ask price.");
        }
        
        Trade newTrade = null;
        
        
        // The quantity of the topBid and topAsk are equal.
        if (bestBid.getQuantity() == bestAsk.getQuantity()) {
            newTrade = new Trade(bestBid.getQuantity(), bestAsk.getPrice());
            fillFullBuyOrder(bestBid);
            fillFullSellOrder(bestAsk);
            tradeDao.addTrade(newTrade);
        }
        
        // The quantity of the topBid is greatest, so is only partially filled.
        else if (bestBid.getQuantity() > bestAsk.getQuantity()) {
            newTrade = new Trade(bestAsk.getQuantity(), bestAsk.getPrice());
            fillPartialBuyOrder(bestBid, bestAsk);
            fillFullSellOrder(bestAsk);
            tradeDao.addTrade(newTrade);
        }
        
        // The quantity of the topAsk is greatest, so is only partially filled.
        else if (bestBid.getQuantity() < bestAsk.getQuantity()) {
            newTrade = new Trade(bestBid.getQuantity(), bestAsk.getPrice());
            fillFullBuyOrder(bestBid);
            fillPartialSellOrder(bestBid, bestAsk);
            tradeDao.addTrade(newTrade);
        }
        
        return newTrade;
    }

    /**
     * Cycles through the order book until either the buyOrders list OR sellOrders list is empty.
     * @return a boolean representing whether any match was null, i.e. unsuccessful.
     */
    @Override
    public int matchAllOrders() throws NoMatchException, PersistenceException {
        int matchCount = 0;
        try {
            while (!checkOrderEmpty()) {
                if (match() != null) {
                    matchCount++;
                }
            }
            return matchCount;
        }
        catch (NoMatchException e) {
            return matchCount;
            
        }
    }

    /**
     * 
     * @param buyOrder
     * @return 
     */
    @Override
    public BuyOrder addBuyOrder(BuyOrder buyOrder) throws PersistenceException {
        if (buyOrder==null) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        buyOrderDao.addBuyOrder(buyOrder);
        return buyOrder;
    }

    /**
     * 
     * @param sellOrder
     * @return 
     */
    @Override
    public SellOrder addSellOrder(SellOrder sellOrder) throws PersistenceException {
        if (sellOrder==null) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        sellOrderDao.addSellOrder(sellOrder);
        return sellOrder;
    }

    /**
     * In the case of a full buy order fulfilment, the BuyOrder is removed from the records.
     * @param buyOrder the BuyOrder object that has been matched.
     */
    private void fillFullBuyOrder(Order buyOrder) throws PersistenceException {
        buyOrderDao.removeBuyOrder(buyOrder);
    }

    /**
     * In the case of a full sell order fulfilment, the SellOrder is removed from the records.
     * @param sellOrder the SellOrder object that has been matched.
     */
    private void fillFullSellOrder(Order sellOrder) throws PersistenceException {
        sellOrderDao.removeSellOrder(sellOrder);
    }

    /**
     * In the case of a partial buy order fulfilment, the BuyOrder entry in the order book is updated
     * to contain only the remaining, unfulfilled quantity.
     * @param buyOrder the BuyOrder object that has been matched but only partially filled.
     * @param sellOrder the SellOrder object that has been matched and fully filled.
     */
    private void fillPartialBuyOrder(Order buyOrder, Order sellOrder) throws PersistenceException {
        int quantity = buyOrder.getQuantity() - sellOrder.getQuantity();
        
        BuyOrder partialBuyOrder = new BuyOrder();
        partialBuyOrder.setId(buyOrder.getId());
        partialBuyOrder.setPrice(buyOrder.getPrice());
        partialBuyOrder.setQuantity(quantity);          // The only value changed is the quantity
        
        // The old copy is removed and the updated copy added.
        buyOrderDao.removeBuyOrder(buyOrder);
        buyOrderDao.addBuyOrder(partialBuyOrder);
    }

    /**
     * In the case of a partial sell order fulfilment, the SellOrder entry in the order book is updated
     * to contain only the remaining, unfulfilled quantity.
     * @param buyOrder the SellOrder object that has been matched but only partially filled.
     * @param sellOrder the BuyOrder object that has been matched and fully filled.
     */
    private void fillPartialSellOrder(Order buyOrder, Order sellOrder) throws PersistenceException {
        int quantity = sellOrder.getQuantity() - buyOrder.getQuantity();
        
        SellOrder partialSellOrder = new SellOrder();
        partialSellOrder.setId(sellOrder.getId());
        partialSellOrder.setPrice(sellOrder.getPrice());
        partialSellOrder.setQuantity(quantity);          // The only value changed is the quantity
        
        // The old copy is removed and the updated copy added.
        sellOrderDao.removeSellOrder(sellOrder);
        sellOrderDao.addSellOrder(partialSellOrder);
    }

    @Override
    public Trade getTrade(int id) throws PersistenceException{
        return tradeDao.getTrade(id);
    }

    @Override
    public List<Trade> getTrades() throws PersistenceException{
        return tradeDao.getTrades();
    }

    @Override
    public void updateGlobalOrderId() throws PersistenceException {
        int max = Math.max(buyOrderDao.getLastBuyOrderId(), sellOrderDao.getLastSellOrderId());
        Order.setGlobalId(max + 1);
    }
    
    @Override
    public void updateGlobalTradeId() throws PersistenceException {
        Trade.setGlobalId(tradeDao.getLastTradeId() + 1);
    }
    
    public void generateOrders(int amount) throws PersistenceException {
        updateGlobalOrderId();
        updateGlobalTradeId();
        for (int i=0; i<amount; i++){
            buyOrderDao.addBuyOrder(new BuyOrder());
            sellOrderDao.addSellOrder(new SellOrder());
        }  
    }
}
