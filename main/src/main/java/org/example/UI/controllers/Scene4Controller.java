package org.example.UI.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.example.UI.MainApp;

public class Scene4Controller {

    private MainApp mainApp;

    @FXML
    private TextArea notesTextArea;

    @FXML
    private Button backButton;

    @FXML
    private Button confirmButton;

    @FXML
    private void initialize() {
        backButton.setOnAction(event -> {
            try {
                mainApp.showScene3();
            } catch (Exception ignore) {
            }
        });

        confirmButton.setOnAction(event -> {
            // Logica per conferma finale
            System.out.println("Note inserite: " + notesTextArea.getText());
            // Qui potresti chiudere l'applicazione o mostrare un messaggio
        });
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

}