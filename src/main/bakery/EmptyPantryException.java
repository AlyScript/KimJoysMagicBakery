package bakery;

/**
 * Represents an exception that is thrown when an operation involving the pantry cannot be completed due to it being empty.
 * This exception is typically thrown to signal situations in a bakery system where an expected ingredient or set of ingredients
 * is unavailable, causing the operation (like preparing a recipe or restocking) to fail.
 *
 * This exception is meant to provide clear error information to the system or the end user, facilitating debugging and error handling
 * in scenarios where pantry items are crucial for operations.
 *
 * @author Adam Aly
 * @version 1.0
 * @since 2024
 */
public class EmptyPantryException extends java.lang.RuntimeException {
    
    /**
     * Constructs a new EmptyPantryException with the specified detail message and cause.
     * 
     * @param msg the detail message. The detail message is saved for later retrieval by the {@link Throwable#getMessage()} method.
     * @param e the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A {@code null} value
     *          is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public EmptyPantryException(String msg, Throwable e) {
        super();
    }
    
}
