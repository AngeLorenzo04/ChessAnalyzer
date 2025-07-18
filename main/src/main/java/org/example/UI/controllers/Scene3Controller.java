package org.example.UI.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.example.UI.MainApp;

import java.io.IOException;

public class Scene3Controller {

    private MainApp mainApp;

    @FXML
    private ComboBox<String> matchComboBox;

    @FXML
    private Button nextButton;

    @FXML
    private void initialize() {
        // Esempio: popolare la ComboBox con alcune partite
        matchComboBox.getItems().addAll("Partita A", "Partita B", "Partita C");

        nextButton.setOnAction(event -> {
            if (matchComboBox.getValue() != null) {
                try {
                    mainApp.showScene4();
                } catch (Exception ignore) {}
            }
        });
    }

    @FXML
    private void navigateToNextScene() {
        try {
            mainApp.showScene4();
        } catch (IOException ignore) {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void navigateToPreviusScene() {
        try {
            mainApp.showScene1();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

}