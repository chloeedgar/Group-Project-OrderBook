package orderbook.ui;

import java.math.BigDecimal;
import java.util.List;

import orderbook.dto.Order;
import orderbook.dto.TableList;
import orderbook.dto.Trade;


public class OrderBookView {
    
    UserIO io;
    
    public OrderBookView(UserIO io) {
        this.io = io;
    }

    
    public int displayMainMenuAndGetSelection() {
        io.print("****************************************");
        io.print("*<<Order Book Application>>");
        io.print("* 1. View orderbook");
        io.print("* 2. Display orderbook stats");
        io.print("* 3. Match an order");
        io.print("* 4. Match all orders");
        io.print("* 5. View trade");
        io.print("* 6. View all trades");
        io.print("* 7. Exit");
        
        return io.readInt("Please make a selection from the menu above.",1,7);
    }
    
    //-----------------------------DisplayOrderBook-----------------------------------

    public void displayViewOrderBookBanner() {
        io.print("================================================= View Orderbook =================================================");
    }
    
    public String displayOrderBook(List<List<Order>> orderBook) {
        //Display the buy orders on the left and sell orders on the 
        //right, both ordered by price (already ordered).
        
        //separate orderbook into buy orders and sell orders
        List<Order> buyOrders = orderBook.get(0);
        List<Order> sellOrders = orderBook.get(1);
        
        //get the maximum of the sizes of the two lists to use for the for loop
        int maxSize = Math.max(buyOrders.size(), sellOrders.size());
        //get the max index allowed for each of the lists
        int buyOrdersMaxIndex = buyOrders.size()-1;
        int sellOrdersMaxIndex = sellOrders.size()-1;

        int ordersPerPage = 15; //can be changed depending on how it looks

        if (ordersPerPage > maxSize){
            ordersPerPage = maxSize;
        }
        int totalPages;
        if(maxSize % ordersPerPage ==0){
            totalPages = maxSize/ordersPerPage;
        } else {
            totalPages = maxSize/ordersPerPage+1;
        }

        int pageCount = 1;
        int beginIndex = 0;
        String selection;
        
        while (pageCount<=totalPages) {

                int endIndex = beginIndex+ordersPerPage;
                displayOrderBookTable(orderBook, beginIndex, endIndex);
                io.print("Viewing page: " + pageCount + "/" +totalPages);
                if (pageCount == totalPages && pageCount>1) {
                    selection = io.readString("Press 'p' for previous page, 'e' to exit.");
                }else if (pageCount == 1 && totalPages>1){ 
                    selection = io.readString("Press 'n' for next page, 'e' to exit.");
                } else if (pageCount>1 && totalPages>1 && pageCount<totalPages) {
                    selection = io.readString("Press 'n' for next page, 'p' for previous page, 'e' to exit.");
                } else { //there is only one page to view
                    selection = io.readString("Press 'e' to exit.");
                }

                if (selection.equalsIgnoreCase("n")){
                    beginIndex += ordersPerPage;
                    pageCount++;
                } else if (selection.equalsIgnoreCase("p")){
                    beginIndex -= ordersPerPage;
                    pageCount--;
                    if (pageCount<1){ //return to menu if page count goes below 1.
                        break;
                    }
                }else {
                    break; //anything other than n, N, p, P - return to menu
                }
            }
        return io.readString("Please press enter to continue.");
        }
    
        
    public void displayOrderBookTable(List<List<Order>> orderBook, int beginIndex, int endIndex) {
        //separate orderbook into buy orders and sell orders lists
        List<Order> buyOrders = orderBook.get(0);
        List<Order> sellOrders = orderBook.get(1);
        
        //get the max index allowed for each of the lists
        int buyOrdersMaxIndex = buyOrders.size()-1;
        int sellOrdersMaxIndex = sellOrders.size()-1;
        
        //create a table with two columns
        TableList table = new TableList(2, "Buy Orders", "Sell Orders");

        for (int index=beginIndex; index<endIndex; index++) {
            if (index<=buyOrdersMaxIndex && index<=sellOrdersMaxIndex){
                //there are buy orders and sell orders left, print both
                table.addRow(buyOrders.get(index).toString(), sellOrders.get(index).toString());
            } else if (index<=(buyOrdersMaxIndex) && index>sellOrdersMaxIndex) {
                //there are buy orders left but we have got through all the sell orders, only print buy orders
                table.addRow(buyOrders.get(index).toString(),"");
            } else if (index>buyOrdersMaxIndex && index<=sellOrdersMaxIndex) {
                //there are sell orders left but we have got through all the buy orders, only print sell orders
                table.addRow("",sellOrders.get(index).toString());
            } else { 
                //There are none of either sell orders or buy orders left to print
                table.addRow("","");
            }
        }
        io.print("******************************************** Orderbook for Tesla Stock ********************************************");
        table.print();
    }

    //-----------------------------DisplayStats----------------------------------
    public void displayStatsBanner() {
        io.print("=== View Orderbook Stats ===");
    }
    
    public String displayStats(List<BigDecimal> statsList){
        io.print("==== Orderbook Stats ====");
        io.print("Number of buy orders:  "+ statsList.get(0));
        io.print("Number of sell orders: " + statsList.get(1));
        io.print("Overall buy quantity:  "+ statsList.get(2));
        io.print("Overall sell quantity: " + statsList.get(3));
        io.print("Average buy price:     "+ statsList.get(4));
        io.print("Average sell price:    "+ statsList.get(5));
        
        return io.readString("Please press enter to continue");
    }
    
    //-----------------------------MatchOrder-----------------------------------
    public void displayMatchOrderBanner() {
        io.print("===== Match Order =====");
    }
    
    public String matchOrderBookSuccessBanner(Trade trade) {
        if (trade==null){
            io.print("Match unsuccessful");
        }
        io.print("=== Match Order Success ===");
        io.print(trade.tradeSummary());
        
        return io.readString("Please press enter to continue.");
    }

    //-----------------------------MatchAllOrders-----------------------------------
     public void displayMatchAllOrdersBanner() {
        io.print("=== Match All Orders ===");
        io.print("Matching all orders...");
    }

     public String displayMatchAllOrdersSuccessBanner(int matchCount, boolean orderEmpty){
         //no orders matched
         if (matchCount == 0){ 
             io.print("Match all orders unsuccessful. No orders matched.");
             return io.readString("Please press enter to continue");
         //some orders were matched, but not all
         } else if (orderEmpty == false){
             io.print("=== Match All Orders Unsuccessful ===");
             io.print("Not all orders could be matched.");
             io.print(matchCount + " trades executed.");
             return io.readString("Please press enter to continue");
        //all orders were matched
         } else if (orderEmpty == true) {
             io.print("=== Match All Orders Successful ===");
             io.print("All orders were matched.");
             return io.readString("Please press enter to continue");
         }
         return io.readString("Please press enter to continue");
     }
     
     

    //-----------------------------ViewTrade-----------------------------------
     public void displayViewTradeBanner() {
        io.print("=== View Trade ===");
     }
     public int tradeIdPrompt(){
         return io.readInt("Please enter the ID of the trade you want to view.");
     }
     public String displayTrade(Trade trade) {
         if (trade==null) {
             io.print("No such trade exists.");
             return io.readString("Please press enter to continue");
         } else {
            io.print("================= Trade Summary =================");
            io.print("Trade ID:        " + trade.getTradeId());
            io.print("Execution time:  " + trade.getExecutionTime());
            io.print("Quantity filled: " + trade.getQuantity());
            io.print("Executed price:  " + trade.getPrice());
            return io.readString("Please press enter to continue");
     }
     }
     
    //-----------------------------ViewAllTrades-----------------------------------   
    public void displayViewAllTradesBanner() {
        io.print("============================ View All Trades ============================");
     }

    public String displayAllTrades(List<Trade> allTrades) {
        //First check there are trades to view
        if (allTrades.isEmpty()){
            io.print("No trades to view.");
            return io.readString("Please press enter to continue");
        }
        int tradesPerPage = 10; //decides size of table to be viewed
        if (allTrades.size()<tradesPerPage) {  //make the table smaller if there is less than 10 trades
            tradesPerPage = allTrades.size();
        }
        int totalPages;
        if (allTrades.size() % tradesPerPage==0){
            totalPages = allTrades.size()/tradesPerPage;
        } else {
            totalPages = allTrades.size()/tradesPerPage + 1;
        }
        int pageCount = 1;
        int beginIndex = 0;
        String selection;
        while (pageCount <= totalPages) {
            int endIndex = beginIndex + tradesPerPage;
            //pass list and indexes to display table method
            displayTradeTable(allTrades, beginIndex, endIndex);
            
            io.print("Viewing page: " + pageCount + "/" +totalPages);
            //Next/previous page msg depends on which page you are on 
            if (pageCount == totalPages && pageCount>1) {
                selection = io.readString("Press 'p' for previous page or 'e' to exit.");
            }else if (pageCount == 1 && totalPages>1){ 
                selection = io.readString("Press 'n' for next page or 'e' to exit.");
            } else if (pageCount>1 && totalPages>1 && pageCount<totalPages) {
                selection = io.readString("Press 'n' for next page or 'p' for previous page or 'e' to exit.");
            } else { //there is only one page to view
                selection = io.readString("Press 'e' to exit.");
            }
            //next page, update starting index and page count
            if (selection.equalsIgnoreCase("n")){
                beginIndex += tradesPerPage;
                pageCount++;
            //previous page
            } else if (selection.equalsIgnoreCase("p")){
                beginIndex -= tradesPerPage;
                pageCount--;
                if (pageCount<1){ //return to menu if page count goes below 1.
                    break;
                }
            }else {
                break; //anything other than n, N, p, P - return to menu
                }
        }
        return io.readString("Please press enter to continue");
    }
    
    public void displayTradeTable(List<Trade> allTrades, int beginIndex, int endIndex){
        //get the max index allowed
        int maxIndex = allTrades.size()-1;
        
        //create a table with 4 columns
        TableList tradeTable = new TableList(4, "ID", "Time", "Quantity", "Price");
        
        for (int index=beginIndex; index<endIndex; index++) {
            if (index<=maxIndex){
                tradeTable.addRow(String.valueOf(allTrades.get(index).getTradeId()),
                        allTrades.get(index).getExecutionTime().toString(), 
                        String.valueOf(allTrades.get(index).getQuantity()), 
                        allTrades.get(index).getPrice().toString());
            } else { 
                tradeTable.addRow("","","",""); //row of nothing to fill table space
            }
        }
        io.print("************************* Displaying all Trades *************************");
        tradeTable.print();
    }
    
    
    
    
    //-----------------------------Exit,unknown,error-----------------------------------
    public void displayErrorMessage(String errorMsg) {
        io.print("=== ERROR ===");
        io.print(errorMsg);
    }    
    public void displayExitBanner() {
        io.print("Exiting. Good bye!");
    }
    
    public void displayUnknownCommandBanner() {
        io.print("Unknown command!");
    }

}
