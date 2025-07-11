package org.example.model;

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

}