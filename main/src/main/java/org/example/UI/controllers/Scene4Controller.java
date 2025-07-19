package org.example.UI.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import org.example.API.ChessGame;
import org.example.UI.MainApp;
import org.example.UI.board.ChessGuiApp;
import org.example.analysis.ChessAnalyzer;
import org.example.analysis.PgnToUciConverter;
import org.example.utils.PgnUtils;

import java.io.IOException;
import java.util.List;

public class Scene4Controller {

    private MainApp mainApp;

    @FXML
    private TextArea textArea;

    private ChessGame partitaSelezionata;

    @FXML
    private void navigateToPreviusScene() {
        try {
            mainApp.showScene1();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void navigateToNextScene() throws IOException, InterruptedException {

        if (mainApp != null) {
            System.out.println("=========== ANALISI PARTITA ==========");
            analyze(textArea.getText());
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

    private void analyze(String movesPGN) throws IOException, InterruptedException {

        String INITIAL_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        ChessAnalyzer analyzer = new ChessAnalyzer();


        String cleanMovesPGN = PgnUtils.cleanForCompactFormat(movesPGN);
        List<String> movesUCI  = PgnToUciConverter.convertPgnToUci(cleanMovesPGN);
        String[] arrMovesUCI = new String[movesUCI.size()];
        for(int i = 0; i < movesUCI.size();i++){
            arrMovesUCI[i] = movesUCI.get(i);
        }

        analyzer.startStockfish();
        System.out.println("Stockfish avviato correttamente");

        List<ChessAnalyzer.EvaluationResult> results = analyzer.analyzeMoves(INITIAL_FEN,movesUCI);

        System.out.println(movesUCI);
        if(movesUCI.contains("Z")){
            arrMovesUCI[0] = "Z";
        }
        for(ChessAnalyzer.EvaluationResult result : results){
            System.out.println(result);
        }

        ChessGuiApp board = new ChessGuiApp(arrMovesUCI);
        board.initBoard( mainApp.primaryStage);
    }

    private void updateUI() {

        textArea.setText(partitaSelezionata.pgn());

    }

    public void initData(MainApp mainApp, ChessGame partitaSelezionata)  {
        this.mainApp = mainApp;
        this.partitaSelezionata = partitaSelezionata;
        updateUI();
    }

}