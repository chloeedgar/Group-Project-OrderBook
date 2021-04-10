package orderbook.dao;


import orderbook.dto.BuyOrder;
import orderbook.dto.Order;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import orderbook.exceptions.PersistenceException;

import static org.junit.jupiter.api.Assertions.*;

class BuyOrderDaoTest {

    private final String BUY_ORDER_FILE = "buyOrderFileTest.txt";
    private final String DELIMITER = "::";
    private BuyOrderDao buyOrderDao= new BuyOrderDaoFileImpl();
    private List<Order> buyOrders = new ArrayList<>();

    @Test
    void addBuyOrder() throws PersistenceException {
        loadBuyOrders();
        BuyOrder buyOrder=new BuyOrder();

        boolean buy=buyOrderDao.addBuyOrder(buyOrder);

        assertTrue(buy,"Should be true");

    }

    @Test
    void getAllBuyOrders() {

        loadBuyOrders();
        assertFalse(buyOrders.isEmpty(),"Should be true");
        assertEquals(buyOrders.size(),2,"Should be 2");

    }

    @Test
    void removeBuyOrder() {
      loadBuyOrders();
        Order removed=buyOrders.remove(0);

        assertFalse(buyOrders.contains(removed),"Should be false");
        assertEquals(buyOrders.size(),1,"Should be one");
    }

    @Test
    void editQuantityBuyOrder() {
        BuyOrder buyOrder=new BuyOrder();
        buyOrder.setQuantity(50);


        assertNotNull(buyOrder);
        assertEquals(buyOrder.getQuantity(),50);

    }

    @Test
    void loadBuyOrders(){
        Scanner scanner;

        try {
            scanner = new Scanner( new BufferedReader( new FileReader(BUY_ORDER_FILE) ) );
        }
        catch (Exception e) {
            System.out.println("File not found exception");
            return;
        }

        String currentLine;
        BuyOrder currentBuyOrder;

        // Iterates while new lines exist
        while (scanner.hasNextLine()) {

            currentLine = scanner.nextLine();
            currentBuyOrder = unmarshallBuyOrder(currentLine);

            // Adds the SellOrder object currentSellOrder to memory.
            buyOrders.add(currentBuyOrder);
        }

        scanner.close();
    }




    @Test
    BuyOrder unmarshallBuyOrder(String buyOrderString){
        String[] buyOrderArray = buyOrderString.split(DELIMITER);
        int id = Integer.parseInt(buyOrderArray[0]);
        BigDecimal price = new BigDecimal(buyOrderArray[1]);
        int quantity = Integer.parseInt(buyOrderArray[2]);

        BuyOrder newBuyOrder = new BuyOrder();
        newBuyOrder.setId(id);
        newBuyOrder.setPrice(price);
        newBuyOrder.setQuantity(quantity);

        assertNotNull(newBuyOrder);
        assertEquals(newBuyOrder.getQuantity(),quantity);
        assertEquals(newBuyOrder.getId(),id);
        assertEquals(newBuyOrder.getPrice(),price);

       return newBuyOrder;
    }
}