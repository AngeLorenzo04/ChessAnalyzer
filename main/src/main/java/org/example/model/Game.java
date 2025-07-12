package org.example.model;

import org.example.analysis.StockfishEngine;

import java.util.List;
import java.util.Map;

public class Game {
    private String url;
    private String pgn;
    private Map<String, Object> white;
    private Map<String, Object> black;
    private String winner;
    private String initial_setup;
    private String fen;

    // Getters and setters

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPgn() { return pgn; }
    public void setPgn(String pgn) { this.pgn = pgn; }

    public Map<String, Object> getWhite() { return white; }
    public void setWhite(Map<String, Object> white) { this.white = white; }

    public Map<String, Object> getBlack() { return black; }
    public void setBlack(Map<String, Object> black) { this.black = black; }

    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }

    public String getInitial_setup() { return initial_setup; }
    public void setInitial_setup(String initial_setup) { this.initial_setup = initial_setup; }

    public String getFen() { return fen; }
    public void setFen(String fen) { this.fen = fen; }
    /**
     * Funzione che determina il vincitore di una partita in base al attributo result
     *
     * @return
     **/
    public String determineWinner() {
        String whiteResult = (String) white.get("result");
        String blackResult = (String) black.get("result");

        if ("win".equalsIgnoreCase(whiteResult)) {
            this.winner = "white";
        } else if ("win".equalsIgnoreCase(blackResult)) {
            this.winner = "black";
        } else this.winner = "draw";

        return whiteResult;
    }

    public void analyzeWithStockfish(String stockfishPath, int depth) {
        try (StockfishEngine engine = new StockfishEngine(stockfishPath)) {
            List<String> moves = StockfishEngine.extractMovesFromPGN(this.getPgn());

            // Usa initial_setup + mosse giocate per costruire la posizione
            engine.setPosition(moves, this.getInitial_setup());

            String analysis = engine.readUntil("bestmove");

            System.out.println("\nAnalisi della partita: " + this.getUrl());
            System.out.println("Vincitore: " + this.getWinner());
            System.out.println("Valutazione: " + parseEvaluation(analysis));
            System.out.println("Miglior mossa: " + parseBestMove(analysis));

        } catch (Exception e) {
            System.err.println("Errore nell'analisi: " + e.getMessage());
        }
    }

    private String parseEvaluation(String analysis) {
        for (String line : analysis.split("\n")) {
            if (line.contains("score cp ")) {
                return line.split("score cp ")[1].split(" ")[0];
            }
        }
        return "non disponibile";
    }

    private String parseBestMove(String analysis) {
        for (String line : analysis.split("\n")) {
            if (line.startsWith("bestmove")) {
                return line.split(" ")[1];
            }
        }
        return "non disponibile";
    }
}
