package org.example.UI;// ChessFXApp.java
import javafx.application.Application;
import javafx.stage.Stage;
import org.example.API.ChessCLI;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        ChessCLI cli = new ChessCLI();
        NavigationController navigationController = new NavigationController(primaryStage,cli);
        navigationController.showUsernameScreen();
        primaryStage.setTitle("Chess Analyzer");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}