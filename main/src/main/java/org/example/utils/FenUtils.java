// File: FenUtils.java
package org.example.utils;

/**
 * Classe di utilità per operazioni relative alla notazione FEN (Forsyth-Edwards Notation).
 * Gestisce l'estrazione di informazioni da stringhe FEN.
 */
public class FenUtils {

    /**
     * Determina il colore del giocatore dal FEN.
     *
     * @param fen La stringa FEN della posizione
     * @return "Bianco" o "Nero"
     */
    public static String getPlayerColor(String fen) {
        String[] parts = fen.split(" ");
        return parts[1].equals("w") ? "Bianco" : "Nero";
    }

    /**
     * Ottiene il pezzo in una specifica casella da una posizione FEN.
     *
     * @param fen La stringa FEN della posizione
     * @param square La casella in notazione algebrica (es. "e4")
     * @return Il carattere che rappresenta il pezzo
     */
    public static char getPieceAtSquare(String fen, String square) {
        String[] rows = fen.split(" ")[0].split("/");
        int col = square.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(square.charAt(1));

        String fenRow = rows[row];
        int currentCol = 0;

        for (char c : fenRow.toCharArray()) {
            if (Character.isDigit(c)) {
                currentCol += Character.getNumericValue(c);
            } else {
                if (currentCol == col) return c;
                currentCol++;
            }
        }
        return ' ';
    }
}