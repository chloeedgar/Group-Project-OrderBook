package orderbook.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import orderbook.dto.BuyOrder;
import orderbook.dto.Order;
import orderbook.exceptions.PersistenceException;

public class BuyOrderDaoFileImpl implements BuyOrderDao {
    
    private final String BUY_ORDER_FILE = "buyOrderFile.txt";
    private final String DELIMITER = "::";
    
    private List<Order> buyOrders = new ArrayList<>();
    
    Comparator<Order> compareByPrice = (Order o1, Order o2) -> o1.getPrice().compareTo(o2.getPrice());

    
    @Override
    public boolean addBuyOrder(BuyOrder buyOrder)throws PersistenceException {
        buyOrders.clear();
        loadBuyOrders();
        boolean addBuyOrder = buyOrders.add(buyOrder);
        buyOrders.sort(compareByPrice.reversed());
        writeBuyOrders();
        return addBuyOrder;
    }

    @Override
    public List<Order> getAllBuyOrders()throws PersistenceException {
        buyOrders.clear();
        loadBuyOrders();
        return (ArrayList)(buyOrders);
    }
    
    @Override
    public void removeBuyOrder(Order buyOrder)throws PersistenceException {
        buyOrders.clear();
        loadBuyOrders();
        buyOrders.remove(buyOrder);
        writeBuyOrders();
    }  
    
    public int getLastBuyOrderId() throws PersistenceException {
        loadBuyOrders();
        return buyOrders.stream()
                        .map(o -> o.getId())
                        .reduce(0, Integer::max);
    }
    
    private String marshallBuyOrder(Order buyOrder) {
        String buyOrderString = buyOrder.getId() + DELIMITER;
        buyOrderString += buyOrder.getPrice() + DELIMITER;
        buyOrderString += buyOrder.getQuantity();
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
        
        String buyOrderString;
        
        // Iterates through each SellOrder in the sellOrders list
        for (Order buyOrder: buyOrders) {
            buyOrderString = marshallBuyOrder(buyOrder);
            out.println(buyOrderString);           // Writes the marshalled buyOrder to a new line.
            out.flush();                            // Ensures the line has been written to the file.
        }
        out.close(); 
    }
    
    private BuyOrder unmarshallBuyOrder(String buyOrderString) {
        
        String[] buyOrderArray = buyOrderString.split(DELIMITER);
        int id = Integer.parseInt(buyOrderArray[0]);
        BigDecimal price = new BigDecimal(buyOrderArray[1]);
        int quantity = Integer.parseInt(buyOrderArray[2]);
        
        BuyOrder newBuyOrder = new BuyOrder(id, price, quantity);
        return newBuyOrder;
    }

    private void loadBuyOrders() throws PersistenceException {
        Scanner scanner;
        
        try {
            scanner = new Scanner( new BufferedReader( new FileReader(BUY_ORDER_FILE) ) );    
        }
        catch (Exception e) {
            throw new PersistenceException (
            "Could not load buy order data into memory",e);
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
    
}
