package ui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import ui.StartScreenUI;
import bakery.CustomerOrder;
import bakery.Customers;
import bakery.Ingredient;
import bakery.Layer;
import bakery.MagicBakery;
import bakery.MagicBakery.ActionType;
import bakery.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import util.StringUtils;

public class GameBoardUI {

    private Stage stage;
    private MagicBakery game;
    private Label gameStateLabel; // To display current game state or messages
    private Button drawIngredientButton;
    private Button bakeLayerButton;
    private Button fulfillOrderButton;
    private Button passCardButton;
    private Button refreshPantryButton;
    private ImageManager imageManager = new ImageManager();
    private GridPane gameBoardGrid;
    private Label turnInfoLabel;
    
    private VBox mainLayout;
    private HBox ingredientSection;
    private HBox layerSection;
    private HBox customerSection;
    private HBox actionButtonsSection;
    private VBox bottomSection;
    private HBox playerHandSection;
    private VBox playersSection;
    private boolean isDrawMode = false;
    private boolean isBakeMode = false;
    private boolean isFulfillMode = false;
    private boolean isPassMode = false;
    private boolean isRefreshMode = false;
    private Ingredient selectedIngredient;
    final double targetWidth = 20;  // Set the width you want
    final double targetHeight = 20; // Set the height you want
    private Button saveButton;
    private Button quitButton;

    public GameBoardUI(Stage stage, MagicBakery game) {
        this.stage = stage;
        this.game = game;
        initUI();
    }
    
    private void initUI() {
        gameBoardGrid = new GridPane();
        gameBoardGrid.setHgap(10);
        gameBoardGrid.setVgap(10);
        gameBoardGrid.setAlignment(Pos.CENTER);

        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        ColumnConstraints playerColumn = new ColumnConstraints();
        playerColumn.setPercentWidth(20); // Set the width percentage of the player column
        gameBoardGrid.getColumnConstraints().addAll(column1, column2, playerColumn); // Add more columns as necessary


        setupGameBoardSections(); // This will initialize and layout the game sections

        Scene scene = new Scene(gameBoardGrid, 1000, 400); // Adjust the size as necessary
        // Set the CSS style for the background
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void show() {
        // Simply make the stage visible (or other minor adjustments)
        // All setup is already done in initUI
        stage.show();
    }

    private void setupGameBoardSections() {
        try {
            setupSave();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        setupIngredientsSection();
        setupLayersSection();
        setupCustomersSection();
        setupBottomSection();
        setupPlayerSection();
    }

    private void setupIngredientsSection() {
        // This will be the container for ingredient buttons
        ingredientSection = new HBox(10);
        ingredientSection.setAlignment(Pos.CENTER);
        Label pantryLabel = new Label("Pantry");
        pantryLabel.setStyle("-fx-font-weight: bold;");
        ingredientSection.getChildren().add(pantryLabel);

        // Add the top ingredient
        Ingredient topIngredient = game.getPantryDeck().peek();
        if (topIngredient != null) {
            Image topImage = imageManager.getImage(topIngredient.toString());
            ImageView topImageView = new ImageView(topImage);
            topImageView.setFitWidth(50);
            topImageView.setFitHeight(70);
            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(topImageView);
    
            Label countLabel = new Label("x" + game.getPantryDeck().size());
            countLabel.getStyleClass().add("count-label");
            countLabel.setStyle("-fx-font-weight: bold;");
            countLabel.setTextFill(Color.RED);
            StackPane.setAlignment(countLabel, Pos.TOP_RIGHT);
            // StackPane.setMargin(countLabel, new Insets(5, 5, 0, 0));
            stackPane.getChildren().add(countLabel);
    
            ingredientSection.getChildren().add(stackPane);
        }
    
        // Repopulate the ingredientSection with buttons for each ingredient
        for (Ingredient ingredient : game.getPantry()) {
            Image image = imageManager.getImage(ingredient.toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(targetWidth);
            imageView.setFitHeight(targetHeight);
            Button ingredientButton = new Button();
            ingredientButton.setGraphic(new ImageView(image));
            ingredientButton.setOnAction(e -> handleIngredientSelection(ingredient));
            ingredientSection.getChildren().add(ingredientButton);
        }
        
        // Place the ingredientSection at  row 0 in the GridPane
        gameBoardGrid.add(ingredientSection, 0, 0); // Column 0, Row 1
    }
    
    
    
    private void setupLayersSection() {
        layerSection = new HBox(10);
        layerSection.setAlignment(Pos.CENTER);
        Label layersLabel = new Label("Layers");
        layersLabel.setStyle("-fx-font-weight: bold;");
        layerSection.getChildren().add(layersLabel);
    
        // Here, I'm assuming you have similar methods to get the top layer and its count
        List<Layer> layersList = new ArrayList<>(game.getLayers());
        Layer topLayer = layersList.get(layersList.size() - 1); 
        if (topLayer != null) {
            Image layerImage = imageManager.getImage(topLayer.toString());
            ImageView layerImageView = new ImageView(layerImage);
            layerImageView.setFitWidth(45);
            layerImageView.setFitHeight(60);
    
            StackPane stackPane = new StackPane(layerImageView);
            Label countLabel = new Label("x" + (game.getLayers().size() - game.getBakeableLayers().size()));
            countLabel.getStyleClass().add("count-label");
            countLabel.setStyle("-fx-font-weight: bold;");
            countLabel.setTextFill(Color.RED);
            StackPane.setAlignment(countLabel, Pos.TOP_RIGHT);
    
            stackPane.getChildren().add(countLabel);
            layerSection.getChildren().add(stackPane);
        }
    
        for (Layer layer : game.getBakeableLayers()) {
            // Use ImageManager to get the image for the ingredient
            Image image = imageManager.getImage(layer.toString());
            ImageView imageView = new ImageView(image);
            
            // Set the size you want for the ImageView
            imageView.setFitWidth(targetWidth);
            imageView.setFitHeight(targetHeight);
            
            // Preserve the aspect ratio
            //imageView.setPreserveRatio(true);
            Button layerButton = new Button();
            layerButton.setGraphic(new ImageView(image));
            // Set the action to perform when the ingredient button is clicked
            layerButton.setOnAction(e -> handleLayerSelection(layer));
            layerSection.getChildren().add(layerButton);
        }

        for(int i = game.getBakeableLayers().size(); i < 5; i++) {
            Image image = imageManager.getImage("null_layer");
            ImageView imageView = new ImageView(image);
            layerSection.getChildren().add(imageView);
        }
    
        gameBoardGrid.add(layerSection, 0, 1); // Adjust the row as needed
    }
    
    private void setupPlayerSection() {
        playersSection = new VBox(1);
        playersSection.setPadding(new Insets(10, 0, 0, 20));
        // Label playersLabel = new Label("  Players");
        // playersSection.getChildren().add(playersLabel);
    
        int count = 1;
        for (Player player : game.getPlayers()) {
            // Use ImageManager to get the image for the player
            String curr = " (Current Player)";
            if(player != game.getCurrentPlayer()) {
                curr = "";
            }
            String playerLabelStr = String.format("%s%s", player.toString(), curr);
            Label playerLabel = new Label(playerLabelStr);
            playerLabel.setStyle("-fx-font-weight: bold;");
            String playerStr = String.format("player%d", count);
            playersSection.getChildren().add(playerLabel);
            Image image = imageManager.getImage(playerStr);
            ImageView imageView = new ImageView(image);
            
            // Set the size you want for the ImageView
            imageView.setFitWidth(targetWidth);
            imageView.setFitHeight(targetHeight);
            
            // Preserve the aspect ratio
            //imageView.setPreserveRatio(true);
            Button playerButton = new Button();
            playerButton.setGraphic(new ImageView(image));
            // Set the action to perform when the ingredient button is clicked
            playerButton.setOnAction(e -> handlePlayerSelection(player));
            playersSection.getChildren().add(playerButton);
            count++;
        }
        
        gameBoardGrid.add(playersSection, 2, 0, 1, GridPane.REMAINING); // Column 2, Row 0, ColumnSpan 1, RowSpan GridPane.REMAINING
    }

    private void handlePlayerSelection(Player player) {
        if(isPassMode && selectedIngredient != null) {
            game.passCard(selectedIngredient, player);
            isPassMode = false;
            selectedIngredient = null; // Reset the selected ingredient
            updateGameState();
        }
    }

    private void setupCustomersSection() {
        customerSection = new HBox(10);
        customerSection.setAlignment(Pos.CENTER);
        Label customersLabel = new Label("Customers");
        customersLabel.setStyle("-fx-font-weight: bold;");
        customerSection.getChildren().add(customersLabel);
    
        if (!game.getCustomers().getActiveCustomers().isEmpty()) {
            Customers customer = game.getCustomers();
            Stack<CustomerOrder> customerDeck = (Stack<CustomerOrder>)customer.getCustomerDeck();
            CustomerOrder topCustomer = customerDeck.peek();
            Image customerImage = imageManager.getImage(topCustomer.toString());
            ImageView customerImageView = new ImageView(customerImage);
            StackPane stackPane = new StackPane(customerImageView);
            Label countLabel = new Label("x" + (customerDeck.size()));
            countLabel.getStyleClass().add("count-label");
            countLabel.setStyle("-fx-font-weight: bold;");
            countLabel.setTextFill(Color.RED);
            StackPane.setAlignment(countLabel, Pos.TOP_RIGHT);

            stackPane.getChildren().add(countLabel);
            customerSection.getChildren().add(stackPane);
        }
    
        for (CustomerOrder customerOrder : game.getCustomers().getActiveCustomers()) {
            // Use ImageManager to get the image for the ingredient
            if(customerOrder != null) {
                Image image = imageManager.getImage(customerOrder.toString());
                ImageView imageView = new ImageView(image);
                
                // Preserve the aspect ratio
                //imageView.setPreserveRatio(true);
                Button customerButton = new Button();
                customerButton.setGraphic(new ImageView(image));
                // Disable the button if the customerOrder is not fulfillable
                if (!customerOrder.canFulfill(game.getCurrentPlayer().getHand())) {
                    customerButton.setDisable(true);
                } else {
                    customerButton.setDisable(false);
                }
                // Set the action to perform when the ingredient button is clicked
                customerButton.setOnAction(e -> handleCustomerSelection(customerOrder));
                customerSection.getChildren().add(customerButton);
            } else {
                Image image = imageManager.getImage("null");
                ImageView imageView = new ImageView(image);
                customerSection.getChildren().add(imageView);
            }
        }
    
        gameBoardGrid.add(customerSection, 0, 2); // Adjust the row as needed
    }

    private void handleCustomerSelection(CustomerOrder customerOrder) {
        // Implement the logic that should be triggered when a customer order is selected
        // For example, fulfilling a customer order:
        if(isFulfillMode) {
            if(customerOrder.canGarnish(game.getCurrentPlayer().getHand())) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Garnish Order?");
                alert.setContentText("Do you want to garnish " + customerOrder.toString() + "?");
                ButtonType yesButton = new ButtonType("Yes", ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonData.NO);
                alert.getButtonTypes().setAll(yesButton, noButton);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK){
                    game.fulfillOrder(customerOrder, true);
                } else {
                    game.fulfillOrder(customerOrder, false);
                }
            } else {
                game.fulfillOrder(customerOrder, false);
            }
            isFulfillMode = false;
        }
        // Then update the game state UI
        updateGameState();
    }

    private void handleRefreshPantry() {
        if (game.getAvailableActions().contains(ActionType.REFRESH_PANTRY)) {
            game.refreshPantry();
            updateGameState();
        }
    }

    private void setupBottomSection() {
        actionButtonsSection = new HBox(10);
        actionButtonsSection.setAlignment(Pos.CENTER);
    
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> isRefreshMode = true);
        Button drawButton = new Button("Draw");
        drawButton.setOnAction(e -> isDrawMode = true);
        Button passButton = new Button("Pass Card");
        passButton.setOnAction(e -> isPassMode = true);
        Button bakeButton = new Button("Bake Layer");
        bakeButton.setOnAction(e -> isBakeMode = true);
        Button fulfillButton = new Button("Fulfil Order");
        fulfillButton.setOnAction(e -> isFulfillMode = true);

        if (!game.getAvailableActions().contains(ActionType.REFRESH_PANTRY)) {
            refreshButton.setStyle("-fx-background-color: red; -fx-opacity: 1;");
            refreshButton.setDisable(true);
        }

        if (!game.getAvailableActions().contains(ActionType.DRAW_INGREDIENT)) {
            drawButton.setStyle("-fx-background-color: red; -fx-opacity: 1;");
            drawButton.setDisable(true);
        }

        if (!game.getAvailableActions().contains(ActionType.PASS_INGREDIENT)) {
            passButton.setStyle("-fx-background-color: red; -fx-opacity: 1;");
            passButton.setDisable(true);
        }

        if (!game.getAvailableActions().contains(ActionType.BAKE_LAYER)) {
            bakeButton.setStyle("-fx-background-color: red; -fx-opacity: 1;");
            bakeButton.setDisable(true);
        }

        if (!game.getAvailableActions().contains(ActionType.FULFIL_ORDER)) {
            fulfillButton.setStyle("-fx-background-color: red; -fx-opacity: 1;");
            fulfillButton.setDisable(true);
        }
    
        // Setup the action listeners for each button as needed
    
        actionButtonsSection.getChildren().addAll(refreshButton, drawButton, passButton, bakeButton, fulfillButton);
    
        playerHandSection = new HBox(10); // Assuming there is a method to populate this with the player's hand
        playerHandSection.setAlignment(Pos.CENTER);
        // Populate playerHandSection with cards...
    
        bottomSection = new VBox(10);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.getChildren().addAll(actionButtonsSection, playerHandSection);
    
        // Add the turn information at the top of the bottom section
        String turnInfo = String.format("%s's turn. Actions Remaining: %d/%d", game.getCurrentPlayer().toString(), game.getActionsRemaining(), game.getActionsPermitted());
        turnInfoLabel = new Label(turnInfo); // Update this dynamically as needed
        bottomSection.getChildren().add(0, turnInfoLabel); // Add at the beginning of the VBox

        playerHandSection = new HBox(10);
        playerHandSection.setAlignment(Pos.CENTER);

        // Add a label for the player's hand
        Label handLabel = new Label("Hand:");
        handLabel.setStyle("-fx-font-weight: bold;");
        playerHandSection.getChildren().add(handLabel);

        Map<Ingredient, Integer> handMap = new HashMap<>();
        for (Ingredient ingredient : game.getCurrentPlayer().getHand()) {
            handMap.put(ingredient, handMap.getOrDefault(ingredient, 0) + 1);
        }

        // Add buttons for each ingredient in the player's hand
        for (Ingredient ingredient : handMap.keySet()) {
            Image image = imageManager.getImage(ingredient.toString());
            Button ingredientButton = new Button();
            ingredientButton.setGraphic(new ImageView(image));
            ingredientButton.setOnAction(e -> {
                if (isPassMode) {
                    selectedIngredient = ingredient;
                }
            });
            // Add a label with the count to the top right corner of the button
            Label countLabel = new Label("x" + handMap.get(ingredient));
            countLabel.setTextFill(Color.RED);
            countLabel.setStyle("-fx-font-weight: bold;");

            // Create a StackPane to layer the Button and Label on top of each other
            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(ingredientButton, countLabel);
            StackPane.setAlignment(countLabel, Pos.TOP_RIGHT); // Position the Label at the top right corner of the StackPane

            playerHandSection.getChildren().add(stackPane);
        }

        bottomSection.getChildren().add(playerHandSection);
    
        gameBoardGrid.add(bottomSection, 0, 3); // Add the bottom section to the GridPane at the bottom
    }
    
    private void updateTurnInfo() {
        String turnInfo = String.format("%s's turn. Actions Remaining: %d/%d", game.getCurrentPlayer().toString(), game.getActionsRemaining(), game.getActionsPermitted());
        turnInfoLabel.setText(turnInfo);
    }
    
    // Remember to call setupBottomSection() in your initUI method
    // and updateTurnInfo() in your updateGameState() method

    private void handleIngredientSelection(Ingredient ingredient) {
        // Implement the logic that should be triggered when an ingredient is selected
        // For example, drawing an ingredient from the pantry:
        // Then update the game state UI
        if (isDrawMode) {
            game.drawFromPantry(ingredient.toString());
            isDrawMode = false;
        } else {

        }
        updateGameState();
    }

    private void handleLayerSelection(Layer layer) {
        // Implement the logic that should be triggered when a layer is selected
        // For example, baking a layer:
        if(isBakeMode) {
            game.bakeLayer(layer);
            isBakeMode = false;
        }
        // Then update the game state UI
        updateGameState();
    }

    // private void handleCustomerSelection(CustomerOrder customerOrder) {
    //     // Implement the logic that should be triggered when a customer order is selected
    //     // For example, fulfilling a customer order:
    //     game.fulfillOrder(customerOrder, false);
    //     // Then update the game state UI
    //     updateGameState();
    // }

    private void updateGameState() {
        if(game.getCustomers().getCustomerDeck().size() < 1) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Game Over");
            alert.setContentText("No Customers Left! Game Over!");
            handleQuit();
        }
        if(game.getActionsRemaining() < 1) {
            updateTurnInfo();
            game.endTurn();
        }
        // Update each section of the UI
        updateIngredientsSection();
        updateLayersSection();
        updateCustomersSection();
        updateBottomSection();
        updatePlayersSection();
        
        // ... and other sections as needed
    }
    
    private void updateIngredientsSection() {
        // Clear the ingredientSection
        ingredientSection.getChildren().clear();
    
        // Add the pantry label
        Label pantryLabel = new Label("Pantry");
        pantryLabel.setStyle("-fx-font-weight: bold;");
        ingredientSection.getChildren().add(pantryLabel);
    
        // Add the top ingredient
        Ingredient topIngredient = game.getPantryDeck().peek();
        if (topIngredient != null) {
            Image topImage = imageManager.getImage(topIngredient.toString());
            ImageView topImageView = new ImageView(topImage);
            topImageView.setFitWidth(45);
            topImageView.setFitHeight(60);
            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(topImageView);
    
            Label countLabel = new Label("x" + game.getPantryDeck().size());
            countLabel.getStyleClass().add("count-label");
            countLabel.setStyle("-fx-font-weight: bold;");
            countLabel.setTextFill(Color.RED);
            StackPane.setAlignment(countLabel, Pos.TOP_RIGHT);
            // StackPane.setMargin(countLabel, new Insets(5, 5, 0, 0));
            stackPane.getChildren().add(countLabel);
    
            ingredientSection.getChildren().add(stackPane);
        }
    
        // Repopulate the ingredientSection with buttons for each ingredient
        for (Ingredient ingredient : game.getPantry()) {
            Image image = imageManager.getImage(ingredient.toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(targetWidth);
            imageView.setFitHeight(targetHeight);
            Button ingredientButton = new Button();
            ingredientButton.setGraphic(new ImageView(image));
            ingredientButton.setOnAction(e -> handleIngredientSelection(ingredient));
            ingredientSection.getChildren().add(ingredientButton);
        }
    }

    private void updateBottomSection() {
        // Clear the bottomSection
        bottomSection.getChildren().clear();
        actionButtonsSection.getChildren().clear();
        playerHandSection.getChildren().clear();
    
        // Add the oven label
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> isRefreshMode = true);
        Button drawButton = new Button("Draw");
        drawButton.setOnAction(e -> isDrawMode = true);
        Button passButton = new Button("Pass Card");
        passButton.setOnAction(e -> isPassMode = true);
        Button bakeButton = new Button("Bake Layer");
        bakeButton.setOnAction(e -> isBakeMode = true);
        Button fulfillButton = new Button("Fulfil Order");
        fulfillButton.setOnAction(e -> isFulfillMode = true);

        if (!game.getAvailableActions().contains(ActionType.REFRESH_PANTRY)) {
            refreshButton.setStyle("-fx-background-color: red; -fx-opacity: 1;");
            refreshButton.setDisable(true);
        }

        if (!game.getAvailableActions().contains(ActionType.DRAW_INGREDIENT)) {
            drawButton.setStyle("-fx-background-color: red; -fx-opacity: 1;");
            drawButton.setDisable(true);
        }

        if (!game.getAvailableActions().contains(ActionType.PASS_INGREDIENT)) {
            passButton.setStyle("-fx-background-color: red; -fx-opacity: 1;");
            passButton.setDisable(true);
        }

        if (!game.getAvailableActions().contains(ActionType.BAKE_LAYER)) {
            bakeButton.setStyle("-fx-background-color: red; -fx-opacity: 1;");
            bakeButton.setDisable(true);
        }

        if (!game.getAvailableActions().contains(ActionType.FULFIL_ORDER)) {
            fulfillButton.setStyle("-fx-background-color: red; -fx-opacity: 1;");
            fulfillButton.setDisable(true);
        }
    
        // Setup the action listeners for each button as needed
    
        actionButtonsSection.getChildren().addAll(refreshButton, drawButton, passButton, bakeButton, fulfillButton);
    
        playerHandSection = new HBox(10); // Assuming there is a method to populate this with the player's hand
        playerHandSection.setAlignment(Pos.CENTER);
        // Populate playerHandSection with cards...
    
        bottomSection = new VBox(10);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.getChildren().addAll(actionButtonsSection, playerHandSection);
    
        // Add the turn information at the top of the bottom section
        String turnInfo = String.format("%s's turn. Actions Remaining: %d/%d", game.getCurrentPlayer().toString(), game.getActionsRemaining(), game.getActionsPermitted());
        turnInfoLabel = new Label(turnInfo); // Update this dynamically as needed
        turnInfoLabel.setStyle("-fx-font-weight: bold;");
        bottomSection.getChildren().add(0, turnInfoLabel); // Add at the beginning of the VBox

        // Add a label for the player's hand
        Label handLabel = new Label("Hand:");
        handLabel.setStyle("-fx-font-weight: bold;");
        playerHandSection.getChildren().add(handLabel);

        Map<Ingredient, Integer> handMap = new HashMap<>();
        for (Ingredient ingredient : game.getCurrentPlayer().getHand()) {
            handMap.put(ingredient, handMap.getOrDefault(ingredient, 0) + 1);
        }

        // Add buttons for each ingredient in the player's hand
        for (Ingredient ingredient : handMap.keySet()) {
            Image image = imageManager.getImage(ingredient.toString());
            Button ingredientButton = new Button();
            ingredientButton.setGraphic(new ImageView(image));
            ingredientButton.setOnAction(e -> {
                if (isPassMode) {
                    selectedIngredient = ingredient;
                }
            });
            // Add a label with the count to the top right corner of the button
            Label countLabel = new Label("x" + handMap.get(ingredient));
            countLabel.setTextFill(Color.RED);
            countLabel.setStyle("-fx-font-weight: bold;");

            // Create a StackPane to layer the Button and Label on top of each other
            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(ingredientButton, countLabel);
            StackPane.setAlignment(countLabel, Pos.TOP_RIGHT); // Position the Label at the top right corner of the StackPane

            playerHandSection.getChildren().add(stackPane);
        }

        refreshButton.setOnAction(e -> handleRefreshPantry());

        gameBoardGrid.add(bottomSection, 0, 3); // Add the bottom section to the GridPane at the bottom
    }

    private void updateLayersSection() {
        layerSection.getChildren().clear();

        Label layersLabel = new Label("Layers");
        layersLabel.setStyle("-fx-font-weight: bold;");
        layerSection.getChildren().add(layersLabel);
    
        // Here, I'm assuming you have similar methods to get the top layer and its count
        List<Layer> layersList = new ArrayList<>(game.getLayers());
        Layer topLayer = layersList.get(layersList.size() - 1); 
        if (topLayer != null) {
            Image layerImage = imageManager.getImage(topLayer.toString());
            ImageView layerImageView = new ImageView(layerImage);
            layerImageView.setFitWidth(45);
            layerImageView.setFitHeight(60);
    
            StackPane stackPane = new StackPane(layerImageView);
            Label countLabel = new Label("x" + (game.getLayers().size() - game.getBakeableLayers().size()));
            countLabel.setTextFill(Color.RED);
            countLabel.getStyleClass().add("count-label");
            countLabel.setStyle("-fx-font-weight: bold;");
            StackPane.setAlignment(countLabel, Pos.TOP_RIGHT);
            // StackPane.setMargin(countLabel, new Insets(5, 5, 0, 0));
    
            stackPane.getChildren().add(countLabel);
            layerSection.getChildren().add(stackPane);
        }
    
        for (Layer layer : game.getBakeableLayers()) {
            // Use ImageManager to get the image for the ingredient
            Image image = imageManager.getImage(layer.toString());
            ImageView imageView = new ImageView(image);
            
            // Set the size you want for the ImageView
            imageView.setFitWidth(targetWidth);
            imageView.setFitHeight(targetHeight);
            
            // Preserve the aspect ratio
            //imageView.setPreserveRatio(true);
            Button layerButton = new Button();
            layerButton.setGraphic(new ImageView(image));
            // Set the action to perform when the ingredient button is clicked
            layerButton.setOnAction(e -> handleLayerSelection(layer));
            layerSection.getChildren().add(layerButton);
        }

        for(int i = game.getBakeableLayers().size(); i < 5; i++) {
            Image image = imageManager.getImage("null_layer");
            ImageView imageView = new ImageView(image);
            layerSection.getChildren().add(imageView);
        }
    }

    private void updatePlayersSection() {
        playersSection.getChildren().clear();
        // Create a new VBox for the players section
    
        int count = 1;
        for (Player player : game.getPlayers()) {
            // Use ImageManager to get the image for the player
            String curr = " (Current Player)";
            if(player != game.getCurrentPlayer()) {
                curr = "";
            }
            String playerLabelStr = String.format("%s%s", player.toString(), curr);
            Label playerLabel = new Label(playerLabelStr);
            playerLabel.setStyle("-fx-font-weight: bold;");
            String playerStr = String.format("player%d", count);
            playersSection.getChildren().add(playerLabel);
            Image image = imageManager.getImage(playerStr);
            ImageView imageView = new ImageView(image);
            
            // Set the size you want for the ImageView
            imageView.setFitWidth(targetWidth);
            imageView.setFitHeight(targetHeight);
            
            // Preserve the aspect ratio
            //imageView.setPreserveRatio(true);
            Button playerButton = new Button();
            playerButton.setGraphic(new ImageView(image));
            // Set the action to perform when the ingredient button is clicked
            playerButton.setOnAction(e -> handlePlayerSelection(player));
            playersSection.getChildren().add(playerButton);
            count++;
        }
    }

    private void updateCustomersSection() {
        customerSection.getChildren().clear();
        Label customersLabel = new Label("Customers");
        customersLabel.setStyle("-fx-font-weight: bold;");
        customerSection.getChildren().add(customersLabel);
        Customers customer = game.getCustomers();
    
        if (!game.getCustomers().getCustomerDeck().isEmpty()) {
            Stack<CustomerOrder> customerDeck = (Stack<CustomerOrder>)customer.getCustomerDeck();
            CustomerOrder topCustomer = customerDeck.peek();
            Image customerImage = imageManager.getImage(topCustomer.toString());
            ImageView customerImageView = new ImageView(customerImage);
            StackPane stackPane = new StackPane(customerImageView);
            Label countLabel = new Label("x" + (customerDeck.size()));
            countLabel.setStyle("-fx-font-weight: bold;");
            countLabel.setTextFill(Color.RED);
            countLabel.getStyleClass().add("count-label");
            StackPane.setAlignment(countLabel, Pos.TOP_RIGHT);
            // StackPane.setMargin(countLabel, new Insets(5, 5, 0, 0));

            stackPane.getChildren().add(countLabel);
            customerSection.getChildren().add(stackPane);
        }
    
        for (CustomerOrder customerOrder : customer.getActiveCustomers()) {
            // Use ImageManager to get the image for the ingredient
            if(customerOrder != null) {
                Image image = imageManager.getImage(customerOrder.toString());
                ImageView imageView = new ImageView(image);
                
                // Preserve the aspect ratio
                //imageView.setPreserveRatio(true);
                Button customerButton = new Button();
                customerButton.setGraphic(new ImageView(image));
                // Disable the button if the customerOrder is not fulfillable
                if (!customerOrder.canFulfill(game.getCurrentPlayer().getHand())) {
                    customerButton.setDisable(true);
                } else {
                    customerButton.setDisable(false);
                }
                // Set the action to perform when the ingredient button is clicked
                customerButton.setOnAction(e -> handleCustomerSelection(customerOrder));
                customerSection.getChildren().add(customerButton);
            } else {
                Image image = imageManager.getImage("null");
                ImageView imageView = new ImageView(image);
                customerSection.getChildren().add(imageView);
            }
        }
    }

private void setupSave() throws IOException{
    // Create the buttons
    saveButton = new Button("Save");
    saveButton.setStyle("-fx-background-color: #008000; -fx-background-color: #008000; -fx-text-fill: #FFFFFF;");
    saveButton.setOnMouseEntered(e -> saveButton.setStyle("-fx-background-color: #006400; -fx-text-fill: #FFFFFF;"));
    saveButton.setOnMouseExited(e -> saveButton.setStyle("-fx-background-color: #008000; -fx-text-fill: #FFFFFF;"));
    
    quitButton = new Button("Quit");
    quitButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: #FFFFFF;");
    quitButton.setOnMouseEntered(e -> quitButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: #FFFFFF;"));
    quitButton.setOnMouseExited(e -> quitButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: #FFFFFF;"));

    // Set the actions for the buttons
    saveButton.setOnAction(e -> {
        try {
            handleSave();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    });
    quitButton.setOnAction(e -> {
        handleQuit();
    });

    // Create the Info button
    Button infoButton = new Button("Info");
    infoButton.setStyle("-fx-background-color: #0000FF; -fx-text-fill: #FFFFFF;");
    infoButton.setOnMouseEntered(e -> infoButton.setStyle("-fx-background-color: #00008B; -fx-text-fill: #FFFFFF;"));
    infoButton.setOnMouseExited(e -> infoButton.setStyle("-fx-background-color: #0000FF; -fx-text-fill: #FFFFFF;"));

    // Set the action for the Info button
    infoButton.setOnAction(e -> {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText("Current Customers");
        ArrayList<CustomerOrder> customerList = new ArrayList<>();
        String result = "";
        for(CustomerOrder customerOrder : game.getCustomers().getActiveCustomers()) {
            if(customerOrder != null) {
                customerList.add(customerOrder);
            }
        }
        int len = customerList.size()-1;
        for(int i=0; i<customerList.size()-1; i++) {
            result += customerList.get(i).toString().toUpperCase() + " || Recipe: [" + customerList.get(i).getRecipeDescription() + "] || Garnish: [" + customerList.get(i).getGarnishDescription() + "]\n";
        }
        result += customerList.get(len).toString().toUpperCase() + " || Recipe: [" + customerList.get(len).getRecipeDescription() + "] || Garnish: [" + customerList.get(len).getGarnishDescription() + "]";
        alert.setContentText(result);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setPrefSize(680, 250);
        dialogPane.setStyle("-fx-font-weight: bold; -fx-font-size: 1.3em; -fx-text-fill: red;");
        alert.showAndWait();
    });

    // Create a container for the buttons
    HBox buttonBox = new HBox(10); // 10 pixel spacing between buttons
    buttonBox.getChildren().addAll(saveButton, quitButton, infoButton);

    AnchorPane anchorPane = new AnchorPane(buttonBox);

// Set the AnchorPane constraints so that the HBox is aligned to the top right
    AnchorPane.setTopAnchor(buttonBox, 0.0);
    AnchorPane.setRightAnchor(buttonBox, 0.0);

    // Add the AnchorPane to the top right cell of your GridPane
    gameBoardGrid.add(anchorPane, 3, 0); // Adjust the column and row indices as needed
}

// private void updateSaveLoad() {
//     // Enable or disable the buttons based on the game state
//     // Assuming game is your Game object
//     saveButton.setDisable(!game.canSave());
//     quitButton.setDisable(!game.canQuit());
// }

private void handleSave() throws IOException{
    // Save the game
    File file = new File("savefile.txt");
    game.saveState(file);
    
    // StartScreenUI mainMenu = new StartScreenUI(stage);
    // Scene mainMenuScene = new Scene(((Scene) mainMenu).getRootScene(), 800, 600); // Replace 800 and 600 with desired width and height
    // stage.setScene(mainMenuScene);
}

private void handleQuit() {
    new StartScreenUI(stage);
}

}
