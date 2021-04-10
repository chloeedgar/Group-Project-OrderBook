package orderbook.dto;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;

public class Order {

   private static int globalId = 0;
   private int id;
   private BigDecimal price;
   private int quantity;
   private Random random = new Random();
   
   
   public Order(int id, BigDecimal price, int quantity) {
       this.id = id;
       this.price = price;
       this.quantity = quantity;
   }

   public Order() {
        this.id = globalId++;
        this.price = BigDecimal.valueOf(Double.parseDouble(String.valueOf(190+random.nextDouble())));
        this.quantity = random.nextInt(50-20) + 20;
    }
    
    public static void setGlobalId(int id) {
        globalId = id;
    }
    public static int getGlobalId() {
        return globalId;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    

    public int getId() {
        return id;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", price=" + price + ", quantity=" + quantity + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.id;
        hash = 97 * hash + Objects.hashCode(this.price);
        hash = 97 * hash + this.quantity;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Order other = (Order) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.quantity != other.quantity) {
            return false;
        }
        if (!Objects.equals(this.price, other.price)) {
            return false;
        }
        return true;
    }
    
    
    
    
}
