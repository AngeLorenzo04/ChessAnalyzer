package org.example.UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.API.ChessArchive;
import org.example.API.ChessGame;
import org.example.UI.controllers.Scene1Controller;
import org.example.UI.controllers.Scene2Controller;
import org.example.UI.controllers.Scene3Controller;
import org.example.UI.controllers.Scene4Controller;

import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Applicazione Multi-Scena");
        showScene1();
    }

    public void showScene1() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scene1.fxml"));
            Parent root = loader.load();

            Scene1Controller controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();
        } catch (IOException ignore) {}
    }

    public void showScene2(String userName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scene2.fxml"));
            Parent root = loader.load();

            Scene2Controller controller = loader.getController();
            controller.initData(this, userName); // Passa sia mainApp che userName

            primaryStage.getScene().setRoot(root);
        } catch (IOException ignore) {} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void showScene3(ChessArchive archivio) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scene3.fxml"));
            Parent root = loader.load();

            Scene3Controller controller = loader.getController();
            controller.initData(this,archivio);

            primaryStage.getScene().setRoot(root);
        } catch (IOException ignore) {} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void showScene4(ChessGame partita) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scene4.fxml"));
        Parent root = loader.load();

        Scene4Controller controller = loader.getController();
        controller.initData(this,partita);

        primaryStage.getScene().setRoot(root);
    }

    public static void main(String[] args) {
        launch(args);
    }
}