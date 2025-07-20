package org.example.UI.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.API.ChessAPIService;
import org.example.API.ChessArchive;
import org.example.API.ChessDataParser;
import org.example.UI.MainApp;

import java.io.IOException;
import java.util.List;

public class Scene2Controller {
    @FXML
    private Label userNameLabel;
    @FXML private ComboBox<String> archiveComboBox;
    @FXML private Button nextButton;

    private MainApp mainApp;
    private String userName;

    private List<ChessArchive> archivi;

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

    private void updateUI(){
        userNameLabel.setText("Utente: " + userName);

        ChessDataParser parser = new ChessDataParser();
        ChessAPIService apiService = new ChessAPIService();

        String archiviJson = null;
        try {
            archiviJson = apiService.getGameArchives(userName);
        } catch (IOException | InterruptedException ignore) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERRORE: QUALCOSA E' ANDATO STORTO : (");
            alert.setHeaderText(null);
            alert.setContentText("Utente non trovato,\ninserisci il tuo nome utente di chess.com");
            alert.showAndWait();
        }
        this.archivi = parser.parseArchives(archiviJson);
        String archiviStr = archivi.toString().replace("[", "").replace("]", "").trim();
        String[] tmp = archiviStr.split(",\\s*");
        String[] archiviStrArr = new String[tmp.length];
        int k = 0;
        for(int i = tmp.length - 1; i != 0; i--){
            archiviStrArr[k++] = tmp[i];
        }
        archiveComboBox.getItems().addAll(archiviStrArr);
    }

    private void navigateToNextScene() {
        if (mainApp != null) {
            System.out.println(userName + " ha selezionato: " + archiveComboBox.getValue());
            ChessArchive scelto = null;
            for(ChessArchive a: archivi){
                if(a.toString().equals(archiveComboBox.getValue())){
                    scelto = a;
                }
            }
            assert scelto != null;
            mainApp.showScene3(scelto);
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