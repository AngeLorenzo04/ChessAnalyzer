package org.example.UI.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.example.API.ChessAPIService;
import org.example.API.ChessArchive;
import org.example.API.ChessDataParser;
import org.example.API.ChessGame;
import org.example.UI.MainApp;

import java.io.IOException;
import java.util.List;

public class Scene3Controller {

    private MainApp mainApp;

    @FXML
    private ComboBox<String> matchComboBox;

    private ChessArchive archivio;

    private List<ChessGame> parite;


    private void updateUI() throws IOException, InterruptedException {
        // Esempio: popolare la ComboBox con alcune partite

        ChessAPIService apiService = new ChessAPIService();
        ChessDataParser dataParser = new ChessDataParser();
        String gameJson = apiService.getGamesFromArchive(archivio.getUrl());
        this.parite  = dataParser.parseGames(gameJson);
        String gamesStr = parite.toString().replace("[", "").replace("]", "").trim();
        String[] tmp = gamesStr.split(",\\s*");
        String[] gamesStrArr = new String[tmp.length];
        int k = 0;
        for(int i = tmp.length - 1; i != 0 ; i--){
            gamesStrArr[k++] = tmp[i];
        }
        matchComboBox.getItems().addAll(gamesStrArr);

    }

    @FXML
    private void navigateToNextScene() {
        try {

            ChessGame partitaScelta = null;
            for(ChessGame partita : parite){
                if(partita.toString().equals(matchComboBox.getValue())){
                    partitaScelta = partita;
                }
            }

            mainApp.showScene4(partitaScelta);
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

    public void initData(MainApp mainApp, ChessArchive archivio) throws IOException, InterruptedException {
        this.mainApp = mainApp;
        this.archivio = archivio;
        updateUI();
    }

}