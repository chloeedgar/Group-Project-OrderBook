package orderbook.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Trade {
    private static int globalId = 0;
    
    private int tradeId;
    private LocalDateTime executionTime;
    private int quantity;
    private BigDecimal price;

    public Trade(){}

    public Trade(int quantity, BigDecimal price) {
        this.tradeId = globalId++;
        this.executionTime = LocalDateTime.now();
        this.quantity = quantity;
        this.price = price;
    }
    
    public static void setGlobalId(int id) {
        globalId = id;
    }

    public int getTradeId() {
        return tradeId;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }
    
    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }
    
    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }
    
    public void setQuantity(int quanity) {
        this.quantity = quanity;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    

    @Override
    public String toString() {
        return "Trade{" +
                "Id=" + tradeId +
                ", executionTime=" + executionTime +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
    
    public String tradeSummary() {
        return "Trade Summary \n" +
                "Id:             " + tradeId + "\n" +
                "Execution time: " + executionTime +"\n" +
                "Quantity:       " + quantity +"\n" +
                "Price:          " + price;
    }
    
    
    
}
