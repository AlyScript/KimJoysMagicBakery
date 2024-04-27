import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ui.StartScreenUI;

public class BakeryApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        new StartScreenUI(primaryStage);
    }

    public static void main(String[] args) {
        // Launch the application
        launch(args);
    }
}
