package ui;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.image.Image;

    public class ImageManager {
        private Map<String, Image> imageCache = new HashMap<>();
    
        public ImageManager() {
            loadImages();
        }
    
        private void loadImages() {
            // Load and cache images

            // start game image
            imageCache.put("start", new Image(getClass().getResourceAsStream("/ui/images/SHOP.jpg")));

            // player images
            imageCache.put("player1", new Image(getClass().getResourceAsStream("/ui/images/Players/player1.jpg")));
            imageCache.put("player2", new Image(getClass().getResourceAsStream("/ui/images/Players/player2.jpg")));
            imageCache.put("player3", new Image(getClass().getResourceAsStream("/ui/images/Players/player3.jpg")));
            imageCache.put("player4", new Image(getClass().getResourceAsStream("/ui/images/Players/player4.jpg")));
            imageCache.put("player5", new Image(getClass().getResourceAsStream("/ui/images/Players/player1.jpg")));

            /*
             *  1, chocolate bombe, biscuit; butter; chocolate, chocolate
                1, chocolate chip scones, chocolate; pastry, fruit
                1, crumpets, butter; eggs; flour, jam
                1, fondant fancies, icing; sponge, chocolate
                1, millionaire's shortbread, biscuit; butter; chocolate; sugar,
                1, pistachio ganache macarons, biscuit; chocolate,
                1, raspberry pavlova, eggs; fruit; sugar,
                1, shortbread biscuits, biscuit; icing,
                2, bakewell slice, icing; jam; pastry,
                2, chocolate tea cake, biscuit; chocolate; fruit, chocolate
                2, custard tart, crème pât; pastry, fruit
                2, fruit tart, crème pât; fruit; pastry,
                2, jaffa cakes, chocolate; jam; sponge,
                2, lemon drizzle cake, fruit; icing; sponge, fruit
                2, mille feuille, crème pât; fruit; pastry, fruit
                2, profiteroles, chocolate; crème pât; pastry,
                2, swiss roll, jam; sponge,
                3, almond & chocolate torte, chocolate; icing; sponge, chocolate
                3, cocoa crème doughnuts, chocolate; crème pât; sponge; chocolate
                3, danish pastries, crème pât; icing; pastry, fruit
                3, empire biscuit, biscuit; fruit; icing; jam,
                3, frasier cake, crème pât; icing; sponge, fruit
                3, old fashioned trifle, crème pât; jam; sponge, fruit
                3, showstopper cake (2 tiered), icing; sponge; sponge, chocolate; fruit
                3, viennese whirls, biscuit, icing; jam
             */

            // customer images
            imageCache.put("null", new Image(getClass().getResourceAsStream("/ui/images/Customers/null.jpg")));
            imageCache.put("chocolate bombe", new Image(getClass().getResourceAsStream("/ui/images/Customers/bombe.jpg")));
            imageCache.put("chocolate chip scones", new Image(getClass().getResourceAsStream("/ui/images/Customers/scone.jpg")));
            imageCache.put("crumpets", new Image(getClass().getResourceAsStream("/ui/images/Customers/crumpet.jpg")));
            imageCache.put("fondant fancies", new Image(getClass().getResourceAsStream("/ui/images/Customers/fondant.jpg")));
            imageCache.put("millionaire's shortbread", new Image(getClass().getResourceAsStream("/ui/images/Customers/shortbread.jpg")));
            imageCache.put("pistachio ganache macarons", new Image(getClass().getResourceAsStream("/ui/images/Customers/macaron.jpg")));
            imageCache.put("raspberry pavlova", new Image(getClass().getResourceAsStream("/ui/images/Customers/pavlova.jpg")));
            imageCache.put("shortbread biscuits", new Image(getClass().getResourceAsStream("/ui/images/Customers/shortbread_biscuit.jpg")));
            imageCache.put("bakewell slice", new Image(getClass().getResourceAsStream("/ui/images/Customers/bakewell.jpg")));
            imageCache.put("chocolate tea cake", new Image(getClass().getResourceAsStream("/ui/images/Customers/choc_cake.jpg")));
            imageCache.put("custard tart", new Image(getClass().getResourceAsStream("/ui/images/Customers/tart.jpg")));
            imageCache.put("fruit tart", new Image(getClass().getResourceAsStream("/ui/images/Customers/fruit_tart.jpg")));
            imageCache.put("jaffa cakes", new Image(getClass().getResourceAsStream("/ui/images/Customers/jaffa.jpg")));
            imageCache.put("lemon drizzle cake", new Image(getClass().getResourceAsStream("/ui/images/Customers/drizzle.jpg")));
            imageCache.put("mille feuille", new Image(getClass().getResourceAsStream("/ui/images/Customers/mille.jpg")));
            imageCache.put("profiteroles", new Image(getClass().getResourceAsStream("/ui/images/Customers/profiteroles.jpg")));
            imageCache.put("swiss roll", new Image(getClass().getResourceAsStream("/ui/images/Customers/swiss.jpg")));
            imageCache.put("almond & chocolate torte", new Image(getClass().getResourceAsStream("/ui/images/Customers/shortbread.jpg")));
            imageCache.put("cocoa crème doughnuts", new Image(getClass().getResourceAsStream("/ui/images/Customers/donut.jpg")));
            imageCache.put("danish pastries", new Image(getClass().getResourceAsStream("/ui/images/Customers/danish.jpg")));
            imageCache.put("empire biscuit", new Image(getClass().getResourceAsStream("/ui/images/Customers/empire.jpg")));
            imageCache.put("frasier cake", new Image(getClass().getResourceAsStream("/ui/images/Customers/cake.jpg")));
            imageCache.put("old fashioned trifle", new Image(getClass().getResourceAsStream("/ui/images/Customers/trifle.jpg")));
            imageCache.put("showstopper cake (2 tiered)", new Image(getClass().getResourceAsStream("/ui/images/Customers/showstopper.jpg")));
            imageCache.put("viennese whirls", new Image(getClass().getResourceAsStream("/ui/images/Customers/viennese.jpg")));

            

            // ingredient images
            imageCache.put("butter", new Image(getClass().getResourceAsStream("/ui/images/Ingredients/BUTTER.jpg")));
            imageCache.put("helpful duck 𓅭", new Image(getClass().getResourceAsStream("/ui/images/Ingredients/DUCK.jpg")));
            imageCache.put("eggs", new Image(getClass().getResourceAsStream("/ui/images/Ingredients/EGG.jpg")));
            imageCache.put("flour", new Image(getClass().getResourceAsStream("/ui/images/Ingredients/FLOUR.jpg")));
            imageCache.put("fruit", new Image(getClass().getResourceAsStream("/ui/images/Ingredients/FRUIT.jpg")));
            imageCache.put("chocolate", new Image(getClass().getResourceAsStream("/ui/images/Ingredients/CHOCOLATE.jpg")));
            imageCache.put("sugar", new Image(getClass().getResourceAsStream("/ui/images/Ingredients/SUGAR.jpg")));
        
            // layer images
            imageCache.put("null_layer", new Image(getClass().getResourceAsStream("/ui/images/Layers/null_layer.jpg")));
            imageCache.put("biscuit", new Image(getClass().getResourceAsStream("/ui/images/Layers/BISCUIT.jpg")));
            imageCache.put("crème pât", new Image(getClass().getResourceAsStream("/ui/images/Layers/CREME_PAT.jpg")));
            imageCache.put("icing", new Image(getClass().getResourceAsStream("/ui/images/Layers/ICING.jpg")));
            imageCache.put("jam", new Image(getClass().getResourceAsStream("/ui/images/Layers/JAM.jpg")));
            imageCache.put("pastry", new Image(getClass().getResourceAsStream("/ui/images/Layers/PASTRY.jpg")));
            imageCache.put("sponge", new Image(getClass().getResourceAsStream("/ui/images/Layers/SPONGE.jpg")));
        }
    
        public Image getImage(String key) {
            return imageCache.get(key);
        }
}

