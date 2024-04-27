package ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import bakery.MagicBakery;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class StartScreenUI {
    ImageManager imageManager = new ImageManager();

    public StartScreenUI(Stage stage) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        Image image = imageManager.getImage("start");
        BackgroundImage backgroundImage = new BackgroundImage(image, 
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
            BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        root.setBackground(new Background(backgroundImage));
        
        Button startButton = new Button("New Game");
        Button loadButton = new Button("Load Game");
        startButton.setOnAction(e -> new PlayerNameInputUI(stage).show());
        loadButton.setOnAction(e -> {
            try {
                File saveFile = new File("savefile.txt");
                new GameBoardUI(stage, MagicBakery.loadState(saveFile)).show();
            } catch (ClassNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        root.getChildren().add(startButton);
        root.getChildren().add(loadButton);
        Scene scene = new Scene(root, 1000, 400);
        stage.setScene(scene);
        stage.setTitle("Kim Joy's Magic Bakery");
        stage.show();
    }
}

