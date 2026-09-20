package exception;

/**
 * Custom checked exception for booking validation, capacity, and cancellation rules.
 */
public class BookingException extends Exception {

    public BookingException(String message) {
        super(message);
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
