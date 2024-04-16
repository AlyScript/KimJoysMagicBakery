package bakery;

/**
 * Represents an ingredient used in a bakery system. This class provides functionalities to manage and compare ingredient objects.
 * Ingredients are uniquely identified and compared based on their names.
 *
 * The class also includes a special static final ingredient named "HELPFUL DUCK" which can be used in special bakery operations.
 *
 * @author Adam Aly
 * @version 1.0
 * @since 2024
 */
public class Ingredient implements java.io.Serializable, java.lang.Comparable<Ingredient> {
    private String name;
    // Below is the helpful duck card
    public static final Ingredient HELPFUL_DUCK = new Ingredient("helpful duck 𓅭");
    private static final long serialVersionUID = 11085168;

    /**
     * Creates a new Ingredient with the specified name.
     *
     * @param name the name of the ingredient. It must not be null.
     */
    public Ingredient (String name) {
        this.name = name;   
    }

    /**
     * Compares this ingredient with the specified object for equality. The comparison is based on the name of the ingredients.
     *
     * @param obj the object to be compared for equality with this ingredient.
     * @return true if the specified object is an ingredient and has the same name as this ingredient; false otherwise.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Ingredient)) {
            return false;
        }
        Ingredient other = (Ingredient) obj;
        return name.equals(other.name);
    }

    /**
     * Returns a hash code value for the ingredient, which is derived from the ingredient's name.
     *
     * @return a hash code value for this ingredient.
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Returns a string representation of the ingredient, which is the name of the ingredient.
     *
     * @return the name of the ingredient.
     */
    public String toString() {
        return name.strip();
    }

    /**
     * Compares this ingredient with another ingredient by names for ordering. This method provides a natural ordering of ingredients,
     * which is alphabetical based on the ingredient's name.
     *
     * @param other the ingredient to be compared against this ingredient.
     * @return a negative integer, zero, or a positive integer as this ingredient's name is less than, equal to, or greater than the
     *         specified ingredient's name.
     */
    public int compareTo(Ingredient other) {
        return this.name.compareTo(other.name);
    }

}
