
package orderbook.exceptions;

public class NoMatchException extends Exception {

    public NoMatchException(String message) {
        super(message);
    }

    public NoMatchException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
