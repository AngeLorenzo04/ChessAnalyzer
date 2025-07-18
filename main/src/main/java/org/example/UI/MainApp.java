package org.example.UI;// ChessFXApp.java
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.API.ChessCLI;

import java.net.URL;
import java.util.Objects;

public class MainApp extends Application {

//    @Override
//    public void start(Stage primaryStage) {
//        ChessCLI cli = new ChessCLI();
//        NavigationController navigationController = new NavigationController(primaryStage,cli);
//        navigationController.showUsernameScreen();
//        primaryStage.setTitle("Chess Analyzer");
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
@Override
public void start(Stage primaryStage) {
    ChessCLI chessCLI = new ChessCLI();
    NavigationController navController = new NavigationController(primaryStage, chessCLI);

    // Initialize with empty scene
    StackPane root = new StackPane();
    Scene scene = new Scene(root, 800, 600);
    primaryStage.setScene(scene);

    navController.showUsernameScreen();
    primaryStage.setTitle("Chess Analyzer");
    primaryStage.show();
}

    public static void main(String[] args) {
        launch(args);
    }

}