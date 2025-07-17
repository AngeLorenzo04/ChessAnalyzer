package org.example;

import org.example.API.*;
import org.example.analysis.ChessAnalyzer;
import org.example.analysis.PgnToUciConverter;
import org.example.utils.PgnUtils;

import java.io.IOException;
import java.util.List;


public class Main {
    public static void main(String[] args) throws Exception {

        String INITIAL_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        ChessCLI cli = new ChessCLI();
        ChessAPIService apiService = new ChessAPIService();
        ChessDataParser dataParser = new ChessDataParser();
        ChessAnalyzer analyzer = new ChessAnalyzer();

        try{
             String name = cli.promptUsername().get();
             String archiveJSON = apiService.getGameArchives(name);
             List<ChessArchive> archiveList = dataParser.parseArchives(archiveJSON);
             ChessArchive archivioSelezionato = cli.selectArchive().get();

             String gamesJSON = apiService.getGamesFromArchive(archivioSelezionato.getUrl());
             List<ChessGame> games = dataParser.parseGames(gamesJSON);
             ChessGame game = cli.selectGame().get();

             String movesPGN = game.getPgn();
             String cleanMovesPGN = PgnUtils.cleanForCompactFormat(movesPGN);
            List<String> movesUCI  = PgnToUciConverter.convertPgnToUci(cleanMovesPGN);

            analyzer.startStockfish();
            System.out.println("Stockfish avviato correttamente");

            List<ChessAnalyzer.EvaluationResult> results = analyzer.analyzeMoves(INITIAL_FEN,movesUCI);

            System.out.println(movesUCI);
            for(ChessAnalyzer.EvaluationResult result : results){
                System.out.println(result);
            }


       }catch (IOException e) {
            System.err.println("Errore durante l'analisi: " + e.getMessage());
            e.printStackTrace();
       }
    }

}
