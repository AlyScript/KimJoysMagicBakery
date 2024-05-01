package ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import java.util.Random;

import bakery.MagicBakery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.io.FileNotFoundException;

public class PlayerNameInputUI {
    private Stage stage;
    private ImageManager imageManager = new ImageManager();

    public PlayerNameInputUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        Image image = imageManager.getImage("start");
        BackgroundImage backgroundImage = new BackgroundImage(image, 
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
            BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        layout.setBackground(new Background(backgroundImage));

        Label nameLabel = new Label("Select number of players:");
        nameLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        Button twoPlayersButton = new Button("2 Players");
        twoPlayersButton.setOnAction(e -> startGame(2));

        Button threePlayersButton = new Button("3 Players");
        threePlayersButton.setOnAction(e -> startGame(3));

        Button fourPlayersButton = new Button("4 Players");
        fourPlayersButton.setOnAction(e -> startGame(4));

        Button fivePlayersButton = new Button("5 Players");
        fivePlayersButton.setOnAction(e -> startGame(5));

        layout.getChildren().addAll(nameLabel, twoPlayersButton, threePlayersButton, fourPlayersButton, fivePlayersButton);
        Scene scene = new Scene(layout, 1000, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void startGame(int numPlayers) {
        String[] names = new String[numPlayers];
    
        for (int i = 0; i < numPlayers; i++) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Enter Player Name");
            dialog.setHeaderText("Enter name for Player " + (i + 1) + ":");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                names[i] = result.get();
            }
        }
    
        long seed = new Random().nextLong();
        try {
            MagicBakery game = new MagicBakery(seed, "io/ingredients.csv", "io/layers.csv");
            game.startGame(Arrays.asList(names), "customers.csv");
            new GameBoardUI(stage, game).show();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }
}