package org.example.UI.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.UI.MainApp;

public class Scene2Controller {
    @FXML
    private Label userNameLabel;
    @FXML private ComboBox<String> archiveComboBox;
    @FXML private Button nextButton;

    private MainApp mainApp;
    private String userName;

    @FXML
    private void initialize() {
        // Configurazione iniziale UI
        nextButton.setDisable(true);

        archiveComboBox.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> nextButton.setDisable(newVal == null));

        // Permetti di usare Invio sulla ComboBox
        nextButton.setOnAction(e -> {
            if (archiveComboBox.getValue() != null) {
                navigateToNextScene();
            }
        });
    }

    // Inizializza con i dati necessari
    public void initData(MainApp mainApp, String userName) {
        this.mainApp = mainApp;
        this.userName = userName;
        updateUI();
    }

    private void updateUI() {
        userNameLabel.setText("Utente: " + userName);
        archiveComboBox.getItems().addAll(
                "Torneo 2023",
                "Partite amichevoli",
                "Campionato regionale"
        );
    }

    private void navigateToNextScene() {
        if (mainApp != null) {
            System.out.println(userName + " ha selezionato: " + archiveComboBox.getValue());
            mainApp.showScene3();
        } else {
            System.err.println("Errore: mainApp è null!");
            // Mostra un alert all'utente
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Errore di navigazione");
            alert.setContentText("Impossibile procedere. Si è verificato un errore interno.");
            alert.showAndWait();
        }
    }

    @FXML
    private void navigateToPreviusScene() {
        if (mainApp != null) {
            mainApp.showScene1();
        } else {
            System.err.println("Errore: mainApp è null!");
            // Mostra un alert all'utente
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Errore di navigazione");
            alert.setContentText("Impossibile procedere. Si è verificato un errore interno.");
            alert.showAndWait();
        }
    }

}