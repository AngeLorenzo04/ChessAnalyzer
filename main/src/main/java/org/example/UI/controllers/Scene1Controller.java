package org.example.UI.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.UI.MainApp;

public class Scene1Controller {

    private MainApp mainApp;

    @FXML
    private TextField nameField;

    @FXML
    private Button nextButton;

    @FXML
    private void initialize() {
        nextButton.setOnAction(event -> {
            if (!nameField.getText().isEmpty()) {
                try {
                    mainApp.showScene2(nameField.getText());
                } catch (Exception ignore) {
                }
            }
        });
    }

    // Metodo per gestire il tasto Invio
    @FXML
    public void handleEnterKey() {
        if (!nameField.getText().trim().isEmpty()) {
            navigateToNextScene();
        } else {
            nameField.getStyleClass().add("invalid-field");
        }
    }

    @FXML
    private void navigateToNextScene() {
        if (!nameField.getText().trim().isEmpty()) {
            try {
                mainApp.showScene2(nameField.getText().trim());
            } catch (Exception ignore) {}
        } else {
            nameField.getStyleClass().add("invalid-field");
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public String getName() {
        return nameField.getText();
    }
}