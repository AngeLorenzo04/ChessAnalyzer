package org.example.UI;// ChessFXApp.java
import javafx.application.Application;
import javafx.stage.Stage;
import org.example.API.ChessArchive;
import org.example.API.ChessGame;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        NavigationController navigationController = new NavigationController(primaryStage);
        navigationController.showUsernameScreen();
        primaryStage.setTitle("Chess Analyzer");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}