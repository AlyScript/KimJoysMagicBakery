package bakery;

public class Ingredient implements java.io.Serializable, java.lang.Comparable<Ingredient> {
    private String name;
    public static final Ingredient HELPFUL_DUCK = new Ingredient("helpful duck 𓅭");
    private static final long serialVersionUID = 11085168;

    public Ingredient (String name) {
        this.name = name;   
    }

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

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return name.strip();
    }

    @Override
    public int compareTo(Ingredient other) {
        return this.name.compareTo(other.name);
    }

}
