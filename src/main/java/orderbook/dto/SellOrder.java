package orderbook.dto;

import java.math.BigDecimal;

public class SellOrder extends Order{
    
    public SellOrder() {
        super();
    }
    
    public SellOrder(int id, BigDecimal price, int quantity) {
        super(id, price, quantity);
}
    
    @Override
    public String toString() {
        return "Sell OrderID SORD"+super.getId()+":Price:" + super.getPrice()+" Size:"+super.getQuantity();
    }  
    
    
    
}
