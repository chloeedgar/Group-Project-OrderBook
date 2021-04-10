package orderbook;

import orderbook.controller.OrderBookController;
import orderbook.dto.BuyOrder;
import orderbook.dto.SellOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import orderbook.exceptions.PersistenceException;

public class App {
    
    public static void main(String[] args) throws PersistenceException {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        OrderBookController controller = ctx.getBean("controller", OrderBookController.class);      

        controller.run();
    }
    
}
