package orderbook.dto;

import java.math.BigDecimal;

public class BuyOrder extends Order{
    
    public BuyOrder() {
        super();
    }
    
    public BuyOrder(int id, BigDecimal price, int quantity) {
        super(id, price, quantity);
    }

    @Override
    public String toString() {
        return "Buy OrderID BORD"+super.getId()+":Price:" + super.getPrice()+", Size:"+super.getQuantity();
    }
    
   
}
