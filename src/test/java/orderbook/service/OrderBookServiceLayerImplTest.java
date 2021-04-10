package orderbook.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import orderbook.dao.BuyOrderDao;
import orderbook.dao.BuyOrderDaoFileImpl;
import orderbook.dao.SellOrderDao;
import orderbook.dao.SellOrderDaoFileImpl;
import orderbook.dto.BuyOrder;
import orderbook.dto.Order;
import orderbook.dto.SellOrder;
import orderbook.dto.Trade;
import orderbook.exceptions.NoMatchException;
import orderbook.exceptions.PersistenceException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OrderBookServiceLayerImplTest {
    
    private final String BUY_ORDER_FILE = "buyOrderFile.txt";
    private final String SELL_ORDER_FILE = "sellOrderFile.txt";
    private final String DELIMITER = "::";
    
    ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    BuyOrderDao buyOrderDao;
    SellOrderDao sellOrderDao;
    OrderBookServiceLayer service;
    
    
    @BeforeEach
    public void setUp() throws PersistenceException {
        buyOrderDao = ctx.getBean("buyOrderDao", BuyOrderDaoFileImpl.class);
        sellOrderDao = ctx.getBean("sellOrderDao", SellOrderDaoFileImpl.class);
        service = ctx.getBean("service", OrderBookServiceLayerImpl.class);
        
        List<Order> buyOrders = null;
        List<Order> sellOrders = null;
        
        try {
            buyOrders = buyOrderDao.getAllBuyOrders();
            sellOrders = sellOrderDao.getAllSellOrders();
        }
        catch (PersistenceException e) {
            fail("Exception: " + e.getMessage());
        }
        
        // Removes any existing orders from the buy/sell orders lists
        buyOrders.clear();
        writeBuyOrders();
        
        sellOrders.clear();
        writeSellOrders();
        

    }

    @Test
    public void testGetOrderBook() {
        
        // Creates 3 random buy orders and adds them to memory
        BuyOrder buyOrder1 = new BuyOrder();
        BuyOrder buyOrder2 = new BuyOrder();
        BuyOrder buyOrder3 = new BuyOrder();
        
        try {
            buyOrderDao.addBuyOrder(buyOrder1);
            buyOrderDao.addBuyOrder(buyOrder2);
            buyOrderDao.addBuyOrder(buyOrder3);
        }
        catch (PersistenceException e) {
            fail("Exception: " + e.getMessage());
        }
        
        // Creates 2 random sell orders and adds them to memory
        SellOrder sellOrder1 = new SellOrder();
        SellOrder sellOrder2 = new SellOrder();
        
        try {
            sellOrderDao.addSellOrder(sellOrder1);
            sellOrderDao.addSellOrder(sellOrder2);
        }
        catch (PersistenceException e) {
            fail("Exception: " + e.getMessage());
        }
        
        List<List<Order>> orderBook = null;
        List<Order> buyOrders = null;
        List<Order> sellOrders = null;
        
        try {
            orderBook = service.getOrderBook();
            buyOrders = buyOrderDao.getAllBuyOrders();
            sellOrders = sellOrderDao.getAllSellOrders();
        }
        catch (PersistenceException e) {
            fail("Exception: " + e.getMessage());
        }
        
        assertEquals(orderBook.get(0).size(), 3, "The first field of the order book should contain 3 buy orders.");
        assertEquals(orderBook.get(0), buyOrders, "The first list should contain the buy orders added.");
        
        assertEquals(orderBook.get(1).size(), 2, "The second field of the order book should contain 2 sell orders.");
        assertEquals(orderBook.get(1), sellOrders, "The second list should contain the sell orders added.");
    }
    
    @Test
    public void testCheckOrderEmpty() throws PersistenceException {
        
        assertTrue(service.checkOrderEmpty(), "With no orders added, the order book should be considered empty.");
        
        buyOrderDao.addBuyOrder(new BuyOrder());
        
        assertTrue(service.checkOrderEmpty(), "With either list empty, the order book should be considered empty.");
        
        sellOrderDao.addSellOrder(new SellOrder());
        
        assertFalse(service.checkOrderEmpty(), "With both lists non-empty, the order book should not be considered empty.");
        
    }
    
    @Test
    public void testGetStats() {
                
        // Creates 3 buy orders with known values.    
        BuyOrder buyOrder1 = new BuyOrder();
        buyOrder1.setPrice(new BigDecimal("190.5"));
        buyOrder1.setQuantity(40);
        
        BuyOrder buyOrder2 = new BuyOrder();
        buyOrder2.setPrice(new BigDecimal("190.65"));
        buyOrder2.setQuantity(35);
        
        BuyOrder buyOrder3 = new BuyOrder();
        buyOrder3.setPrice(new BigDecimal("190.95"));
        buyOrder3.setQuantity(25);
        
        // Creates 2 sell orders with known values.        
        SellOrder sellOrder1 = new SellOrder();
        sellOrder1.setPrice(new BigDecimal("190.1"));
        sellOrder1.setQuantity(40);
        
        SellOrder sellOrder2 = new SellOrder();
        sellOrder2.setPrice(new BigDecimal("190.2"));
        sellOrder2.setQuantity(20);
        
        // Stores statistics calculated from known values 
        BigDecimal buyAveragePrice = new BigDecimal("190.7");   // The average buy price: 190.5+190.65+190.95/3
        BigDecimal buyTotalQuantity = BigDecimal.valueOf(100);  // The total quantity of buy orders: 40+35+25        
        
        BigDecimal sellAveragePrice = new BigDecimal("190.15"); // The average sell price: 190.1+190.2/2
        BigDecimal sellTotalQuantity = BigDecimal.valueOf(60);  // The total quantity of sell orders: 40+20
        
        try {
            buyOrderDao.addBuyOrder(buyOrder1); 
            buyOrderDao.addBuyOrder(buyOrder2);
            buyOrderDao.addBuyOrder(buyOrder3);

            sellOrderDao.addSellOrder(sellOrder1);
            sellOrderDao.addSellOrder(sellOrder2);
        }
        catch (PersistenceException e) {
            fail("Exception: " + e.getMessage());
        }
        
        List<BigDecimal> stats = null;
        try {
            stats = service.getStats(service.getOrderBook());
        }
        catch (PersistenceException e) {
            fail("Exception: " + e.getMessage());
        }
        
        // Number of orders
        assertEquals(stats.get(0), BigDecimal.valueOf(3), "The first stats element, the number of buy orders, should equal 3");
        assertEquals(stats.get(1), BigDecimal.valueOf(2), "The second stats element, the number of sell orders, should equal 2");
        
        // Total quantity
        assertEquals(stats.get(2), buyTotalQuantity, "The total buy order quantity should equal 100");
        assertEquals(stats.get(3), sellTotalQuantity, "The total sell order quantity should equal 60");
        
        // Average price
        assertEquals(stats.get(4).compareTo(buyAveragePrice), 0, "The average buy order price should equal 190.7");
        assertEquals(stats.get(5).compareTo(sellAveragePrice), 0, "The average buy order price should equal 190.15");
    }
    
    @Test
    public void testMatch() {
        // Creates 2 buy orders with known values.    
        BuyOrder buyOrder1 = new BuyOrder();
        buyOrder1.setPrice(new BigDecimal("190.75"));
        buyOrder1.setQuantity(15);
        
        BuyOrder buyOrder2 = new BuyOrder();
        buyOrder2.setPrice(new BigDecimal("190.65"));
        buyOrder2.setQuantity(25);
        
        // Creates a sell orders with known values.        
        SellOrder sellOrder1 = new SellOrder();
        sellOrder1.setPrice(new BigDecimal("190.4"));
        sellOrder1.setQuantity(40);
        
        try {
            buyOrderDao.addBuyOrder(buyOrder1); 
            buyOrderDao.addBuyOrder(buyOrder2);

            sellOrderDao.addSellOrder(sellOrder1);
        }
        catch (PersistenceException e) {
            fail("Exception: " + e.getMessage());
        }
        
        Trade trade1 = null;
        try {
            trade1 = service.match();
        }
        catch (NoMatchException | PersistenceException e) {
            fail("Exception: " + e.getMessage());
        }
        
        assertEquals(trade1.getPrice().compareTo(new BigDecimal("190.4")), 0, "The first trade should take the price of the sell order, 190.4");
        assertEquals(trade1.getQuantity(), 15, "The first trade should take the quantity of the buy order, 15");
        
        List<Order> buyOrders = null;
        List<Order> sellOrders = null;
        try {
            buyOrders = buyOrderDao.getAllBuyOrders();
            sellOrders = sellOrderDao.getAllSellOrders();
        }
        catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
        
        assertEquals(buyOrders.size(), 1, "After the first trade, only 1 buy order should remain");
        assertEquals(sellOrders.size(), 1, "After the first trade, the sell orders should still contain only 1 order");
        
        //----------
        
        Trade trade2 = null;
        try {
            trade2 = service.match();
        }
        catch (NoMatchException | PersistenceException e) {
            fail("Exception: " + e.getMessage());
        }
        
        assertEquals(trade2.getPrice().compareTo(new BigDecimal("190.4")), 0, "The second trade should take the price of the sell order, 190.4");
        assertEquals(trade2.getQuantity(), 25, "The second trade should take the quantity 25");
        
        try {
            buyOrders = buyOrderDao.getAllBuyOrders();
            sellOrders = sellOrderDao.getAllSellOrders();
        }
        catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
        
        assertEquals(buyOrders.size(), 0, "After the second trade, no buy orders should remain");
        assertEquals(sellOrders.size(), 0, "After the second trade, no sell orders should remain");
    }
    
    @Test
    public void testMatchAllOrders() throws PersistenceException {
        
        // Creates 3 buy orders with known values.    
        BuyOrder buyOrder1 = new BuyOrder();
        buyOrder1.setPrice(new BigDecimal("190.9"));
        buyOrder1.setQuantity(40);
        
        BuyOrder buyOrder2 = new BuyOrder();
        buyOrder2.setPrice(new BigDecimal("190.8"));
        buyOrder2.setQuantity(35);
        
        BuyOrder buyOrder3 = new BuyOrder();
        buyOrder3.setPrice(new BigDecimal("190.7"));
        buyOrder3.setQuantity(25);
        
        // Creates 3 sell orders with known values.        
        SellOrder sellOrder1 = new SellOrder();
        sellOrder1.setPrice(new BigDecimal("190.1"));
        sellOrder1.setQuantity(40);
        
        SellOrder sellOrder2 = new SellOrder();
        sellOrder2.setPrice(new BigDecimal("190.2"));
        sellOrder2.setQuantity(20);
        
        SellOrder sellOrder3 = new SellOrder();
        sellOrder3.setPrice(new BigDecimal("190.3"));
        sellOrder3.setQuantity(20);
        
        try {
            buyOrderDao.addBuyOrder(buyOrder1); 
            buyOrderDao.addBuyOrder(buyOrder2);
            buyOrderDao.addBuyOrder(buyOrder3);

            sellOrderDao.addSellOrder(sellOrder1);
            sellOrderDao.addSellOrder(sellOrder2);
            sellOrderDao.addSellOrder(sellOrder3);
            
            service.matchAllOrders();
        }
        catch (PersistenceException | NoMatchException e) {
            fail("Exception: " + e.getMessage());
        }
        
        // Process state
        assertTrue(service.checkOrderEmpty(), "matchAllOrders should run until the order book is empty.");
        
        // With our known orders, only buyOrder3 with a quantity of 20 should remain after all orders are matched.
        List<List<Order>> orderBook = null;
        try {
            orderBook = service.getOrderBook();
        }
        catch (PersistenceException e) {
            fail("Exception: " + e.getMessage());
        }
        
        assertEquals(orderBook.get(0).size(), 1, "The order book should contain only 1 buy order after all orders are matched.");
        assertEquals(orderBook.get(1).size(), 0, "The order book should contain no sell orders after all orders are matched.");
        
        assertEquals(orderBook.get(0).get(0).getQuantity(), 20, "buyOrder3 should have a quantity of 20 after all orders are matched.");
        
    }
    
    
    
    
    // --------------------------------------------------------------------------------
    
    private String marshallOrder(Order order) {
        String buyOrderString = order.getId() + DELIMITER;
        buyOrderString += order.getPrice() + DELIMITER;
        buyOrderString += order.getQuantity();
        return buyOrderString;
    }
    
    private void writeBuyOrders() throws PersistenceException {
        PrintWriter out;
        // Handles the event that the INVENTORY_FILE does not exist.
        try {
            out = new PrintWriter(new FileWriter(BUY_ORDER_FILE));
        } 
        catch (IOException e) {
            throw new PersistenceException(
            "Could not save buy order data",e);
        }
        
        List<Order> buyOrders = buyOrderDao.getAllBuyOrders();
        String buyOrderString;
        
        // Iterates through each SellOrder in the sellOrders list
        for (Order buyOrder: buyOrders) {
            buyOrderString = marshallOrder(buyOrder);
            out.println(buyOrderString);           // Writes the marshalled buyOrder to a new line.
            out.flush();                            // Ensures the line has been written to the file.
        }
        out.close(); 
    }
    
    private void writeSellOrders() throws PersistenceException {
        
        PrintWriter out;

        // Handles the event that the SELL_ORDER_FILE does not exist.
        try {
            out = new PrintWriter(new FileWriter(SELL_ORDER_FILE));
        } 
        catch (IOException e) {
            throw new PersistenceException(
            "Could not save sell order data",e);
        }
        
        List<Order> sellOrders = sellOrderDao.getAllSellOrders();
        String sellOrderString;
        
        // Iterates through each SellOrder in the sellOrders list
        for (Order sellOrder: sellOrders) {
            sellOrderString = marshallOrder(sellOrder);
            out.println(sellOrderString);           // Writes the marshalled SellOrder to a new line.
            out.flush();                            // Ensures the line has been written to the file.
        }
        out.close(); 
    }
}
