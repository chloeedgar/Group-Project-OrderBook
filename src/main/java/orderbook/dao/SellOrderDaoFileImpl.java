package orderbook.dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import orderbook.dto.Order;
import orderbook.dto.SellOrder;
import orderbook.exceptions.PersistenceException;

public class SellOrderDaoFileImpl implements SellOrderDao {
    
    private final String SELL_ORDER_FILE = "sellOrderFile.txt";
    private final String DELIMITER = "::";
    
    private List<Order> sellOrders = new ArrayList<>();
    
    Comparator<Order> compareByPrice = (Order o1, Order o2) ->
            o1.getPrice().compareTo(o2.getPrice());

    @Override
    public boolean addSellOrder(SellOrder sellOrder) throws PersistenceException{
        sellOrders.clear();
        loadSellOrders();
        boolean outcome = sellOrders.add(sellOrder);
        sellOrders.sort(compareByPrice.reversed());
        writeSellOrders();
        return outcome;
    }

    @Override
    public List<Order> getAllSellOrders() throws PersistenceException {
        sellOrders.clear();
        loadSellOrders();
        return sellOrders;
    }

    @Override
    public void editQuantitySellOrder(Order sellOrder, int quantity) throws PersistenceException {
        sellOrders.clear();
        loadSellOrders();
        sellOrder.setQuantity(quantity);
        sellOrders.sort(compareByPrice.reversed());
        writeSellOrders();
    }
    
    @Override
    public void removeSellOrder(Order sellOrder) throws PersistenceException {
        sellOrders.clear();
        loadSellOrders();
        sellOrders.remove(sellOrder);
        writeSellOrders();
    }
    
    public int getLastSellOrderId() throws PersistenceException {
        loadSellOrders();
        return sellOrders.stream()
                        .map(o -> o.getId())
                        .reduce(0, Integer::max);
    }
    
    private String marshallSellOrder(Order sellOrder) {
        
        String sellOrderString = sellOrder.getId() + DELIMITER;
        sellOrderString += sellOrder.getPrice() + DELIMITER;
        sellOrderString += sellOrder.getQuantity();
        
        return sellOrderString;
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
        String sellOrderString;
        // Iterates through each SellOrder in the sellOrders list
        for (Order sellOrder: sellOrders) {
            sellOrderString = marshallSellOrder(sellOrder);
            out.println(sellOrderString);           // Writes the marshalled SellOrder to a new line.
            out.flush();                            // Ensures the line has been written to the file.
        }
        out.close(); 
    }
    
    private SellOrder unmarshallSellOrder(String sellOrderString) {
        
        String[] sellOrderArray = sellOrderString.split(DELIMITER);
        int id = Integer.parseInt(sellOrderArray[0]);
        BigDecimal price = new BigDecimal(sellOrderArray[1]);
        int quantity = Integer.parseInt(sellOrderArray[2]);
        
        SellOrder newSellOrder = new SellOrder(id, price, quantity);
        return newSellOrder;
    }

    private void loadSellOrders() throws PersistenceException {
        Scanner scanner;
        
        try {
            scanner = new Scanner( new BufferedReader( new FileReader(SELL_ORDER_FILE) ) );    
        }
        catch (FileNotFoundException e) {
            throw new PersistenceException (
            "Could not load sell order data into memory",e);
        }
        
        String currentLine;
        SellOrder currentSellOrder;
        
        // Iterates while new lines exist
        while (scanner.hasNextLine()) {
            
            currentLine = scanner.nextLine();
            currentSellOrder = unmarshallSellOrder(currentLine);
            
            // Adds the SellOrder object currentSellOrder to memory.
            sellOrders.add(currentSellOrder);        
        }
        
        scanner.close();
    }
    
}
