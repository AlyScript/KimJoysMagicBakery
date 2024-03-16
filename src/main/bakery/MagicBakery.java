package bakery;
import util.CardUtils;
import java.util.ArrayList;

public class MagicBakery {
    public MagicBakery(long seed, String ingredientDeckFile, String layerDeckFile) {
        CardUtils util = new CardUtils();
        ArrayList<Layer> list = util.readLayerFile(layerDeckFile);
        System.out.println(list.size());
        /* 
        Ingredient a = new Ingredient("flour");
        Ingredient b = new Ingredient("egg");
        Ingredient c = new Ingredient("chocolate");
        Ingredient d = new Ingredient("milk");

        ArrayList<Ingredient> al = new ArrayList<>();
        al.add(a);
        al.add(b);
        al.add(c);
        al.add(d);
        Layer l = new Layer("base", al);
        CustomerOrder g = new CustomerOrder("adam", al, al, 0);
        System.out.println(g.toString());
        */
    }
}
