package exception;

/**
 * Custom checked exception for event lifecycle, deletion, and validation errors.
 */
public class EventException extends Exception {

    public EventException(String message) {
        super(message);
    }

    public EventException(String message, Throwable cause) {
        super(message, cause);
    }
}
