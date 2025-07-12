package org.example.model;

import org.example.analysis.StockfishEngine;

import java.util.List;
import java.util.Map;

/**
 * Rappresenta una singola partita recuperata dall'API di chess.com.
 * Contiene informazioni come l'URL della partita, il formato PGN e il vincitore.
 */
public class Game {
    private String url;
    private String pgn;                   // Partita in formato PGN (Portable Game Notation)
    private Map<String, Object> white;    // Informazioni sul giocatore bianco
    private Map<String, Object> black;    // Informazioni sul giocatore nero
    private String winner;                // Chi ha vinto ("white", "black", "draw")

    // Getters e Setters

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPgn() {
        return pgn;
    }

    public void setPgn(String pgn) {
        this.pgn = pgn;
    }

    public Map<String, Object> getWhite() {
        return white;
    }

    public void setWhite(Map<String, Object> white) {
        this.white = white;
    }

    public Map<String, Object> getBlack() {
        return black;
    }

    public void setBlack(Map<String, Object> black) {
        this.black = black;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    /**
     *
     * Funzione che determina il vincitore di una partita in base al attributo result
     *
     * **/
    public void determineWinner() {
        String whiteResult = (String) white.get("result");
        String blackResult = (String) black.get("result");

        if ("win".equalsIgnoreCase(whiteResult)) {
            this.winner = "white";
        } else if ("win".equalsIgnoreCase(blackResult)) {
            this.winner = "black";
        } else this.winner = "draw";

    }

    /**
     * Analizza la partita usando il motore Stockfish
     *
     * @param stockfishPath Percorso dell'eseguibile Stockfish
     * @param depth Profondità di analisi (es. 10)
     */
    public void analyzeWithStockfish(String stockfishPath, int depth) {
        try (StockfishEngine engine = new StockfishEngine(stockfishPath)) {
            System.out.println("\nAnalisi della partita: " + this.getUrl());

            List<String> moves = StockfishEngine.extractMovesFromPGN(this.getPgn());
            engine.setPositionFromPGN(this.getPgn());

            for (int i = 0; i < moves.size(); i++) {
                String move = moves.get(i);
                String color = (i % 2 == 0) ? "bianco" : "nero";

                String analysis = engine.analyzePosition(depth);
                String evaluation = parseEvaluation(analysis);
                String bestMove = parseBestMove(analysis);

                System.out.println("\nMossa " + (i + 1) + " (" + color + "): " + move);
                System.out.println("Valutazione: " + evaluation);
                System.out.println("Miglior mossa: " + bestMove);
                System.out.println("Commento: " + generateComment(evaluation, move, bestMove));
            }

        } catch (Exception e) {
            System.err.println("Errore nell'analisi della partita: " + e.getMessage());
        }
    }

    /**
     * Estrae la valutazione dalla risposta di Stockfish
     */
    private String parseEvaluation(String analysis) {
        for (String line : analysis.split("\n")) {
            if (line.contains("score cp ")) {
                return line.split("score cp ")[1].split(" ")[0];
            }
        }
        return "non disponibile";
    }

    /**
     * Estrae la miglior mossa consigliata
     */
    private String parseBestMove(String analysis) {
        for (String line : analysis.split("\n")) {
            if (line.startsWith("bestmove")) {
                return line.split(" ")[1];
            }
        }
        return "non disponibile";
    }

    /**
     * Genera un commento sulla qualità della mossa
     */
    private String generateComment(String evaluationStr, String playedMove, String bestMove) {
        if (playedMove.equals(bestMove)) {
            return "Mossa eccellente! Hai giocato la mossa migliore.";
        } else {
            return "Mossa migliorabile. Avresti dovuto giocare " + bestMove;
        }
    }


}