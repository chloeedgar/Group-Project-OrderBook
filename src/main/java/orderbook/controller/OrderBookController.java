package orderbook.controller;


import java.util.List;
import orderbook.dto.Order;
import orderbook.dto.Trade;
import orderbook.exceptions.NoMatchException;
import orderbook.exceptions.PersistenceException;
import orderbook.ui.OrderBookView;
import orderbook.service.OrderBookServiceLayer;


public class OrderBookController {

    private OrderBookServiceLayer service;
    private OrderBookView view;



    public OrderBookController(OrderBookServiceLayer service, OrderBookView view) {
        this.service = service;
        this.view = view;
    }

    /**
     * The main process of the OrderBookController class.
     */
    public void run() {
        boolean exitProcess = false;
        
        try {
            processGenerateOrders();
        }
        catch (PersistenceException e) {
            processException(e);
        }
        
            // Loops until the user chooses the "Exit" menu option
            while(!exitProcess) {
                try {
                    int choice = view.displayMainMenuAndGetSelection();
                    switch (choice) {
                        case 1 -> processViewOrderBook();       // Menu option "View order book"
                        case 2 -> processDisplayStats();        // Menu option "Display order book statistics"
                        case 3 -> processMatchOrder();          // Menu option "Match an order"
                        case 4 -> processMatchAllOrders();      // Menu option "Match all orders"
                        case 5 -> processViewTrade();           // Menu option "View a trade"
                        case 6 -> processViewAllTrades();       // Menu option "View all trades"
                        case 7 -> exitProcess = true;           // Menu option "Exit"
                        default -> processUnknown();
                    } // End of switch block
                } // End of try block
                catch (PersistenceException | NoMatchException e) {
                    processException(e);
                } 
            } // End of while block
            
        processExit();
    }
    
    private void processGenerateOrders() throws PersistenceException {
        List<List<Order>> orderBook = service.getOrderBook();
        service.generateOrders(1000 - Math.min(orderBook.get(0).size(), orderBook.get(1).size()));
    }
    
    private void processViewOrderBook()throws PersistenceException  {
        view.displayViewOrderBookBanner();
        view.displayOrderBook(service.getOrderBook());
    }
    
    private void processDisplayStats()throws PersistenceException  {
        view.displayStatsBanner();
        view.displayStats(service.getStats(service.getOrderBook()));
    }
    
    private void processMatchOrder() throws NoMatchException, PersistenceException {
        view.displayMatchOrderBanner();
        view.matchOrderBookSuccessBanner(service.match());
    }
    
    private void processMatchAllOrders() throws NoMatchException, PersistenceException {
        view.displayMatchAllOrdersBanner();
        view.displayMatchAllOrdersSuccessBanner(service.matchAllOrders(), service.checkOrderEmpty());
    }
    
    private void processViewTrade() throws PersistenceException {
        view.displayViewTradeBanner();
        Trade trade= service.getTrade(view.tradeIdPrompt());
        view.displayTrade(trade);
    }
    
    private void processViewAllTrades() throws PersistenceException{
        view.displayViewAllTradesBanner();
        view.displayAllTrades(service.getTrades());
    }
    
    private void processUnknown() {
       view.displayUnknownCommandBanner();
    }
    
    private void processExit() {
       view.displayExitBanner();
    }
    
    private void processException(Exception e) {
        view.displayErrorMessage(e.getMessage());
    }
    
}
