package bakery;

/**
 * Represents an exception that indicates a mismatch between required and provided ingredients in bakery operations.
 * This exception is used to signal errors in scenarios where the ingredients presented do not meet the specifications
 * necessary for a particular recipe or operation, such as attempting to complete a recipe with missing or incorrect ingredients.
 * Extending {@link IllegalArgumentException}, it indicates that the arguments passed to a method are inappropriate for its operation.
 *
 * @author Adam Aly
 * @version 1.0
 * @since 2024
 */
public class WrongIngredientsException extends java.lang.IllegalArgumentException{

    /**
     * Constructs a new WrongIngredientsException with a specific message that describes the error in detail.
     * The message typically explains which ingredients are incorrect or missing, providing clarity on the nature of the error.
     *
     * @param msg the detailed message that explains the cause of the exception, intended to provide insights into what went wrong.
     */
    public WrongIngredientsException(String msg) {
        super(msg);
    }
}
