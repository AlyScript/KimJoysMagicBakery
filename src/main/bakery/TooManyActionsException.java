package bakery;

/**
 * Signals that a player or process has attempted to perform more actions than are allowed within a given context or timeframe.
 * This exception is typically thrown in scenarios where game rules or system protocols limit the number of permissible operations
 * to prevent abuse or ensure fair play. It extends {@link IllegalStateException} to indicate that the state of the application
 * does not allow for further actions due to these predefined limits.
 *
 * @author Adam Aly
 * @version 1.0
 * @since 2024
 */
public class TooManyActionsException extends java.lang.IllegalStateException {
    
    /**
     * Constructs a new TooManyActionsException with a detailed message explaining the reason for the exception.
     * The message should clearly describe the excess action attempt and the context in which it occurred.
     *
     */
    public TooManyActionsException() {
        super();
    }

}
