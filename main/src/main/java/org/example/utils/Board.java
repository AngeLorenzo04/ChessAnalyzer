// File: Board.java
package org.example.utils;

/**
 * Rappresenta una scacchiera con stato e operazioni di base.
 * Gestisce la posizione dei pezzi e le mosse.
 */
public class Board {
    private final char[][] squares = new char[8][8];
    private boolean whiteToMove;

    /**
     * Carica una posizione dalla notazione FEN.
     *
     * @param fen La stringa FEN da caricare
     */
    public void loadFromFen(String fen) {
        String[] parts = fen.split(" ");
        String[] rows = parts[0].split("/");

        for (int i = 0; i < 8; i++) {
            int col = 0;
            for (char c : rows[i].toCharArray()) {
                if (Character.isDigit(c)) {
                    int empty = Character.getNumericValue(c);
                    for (int j = 0; j < empty; j++) {
                        squares[i][col++] = ' ';
                    }
                } else {
                    squares[i][col++] = c;
                }
            }
        }
        whiteToMove = parts[1].equals("w");
    }

    /**
     * Verifica a chi spetta il turno.
     *
     * @return true se è il turno del bianco, false altrimenti
     */
    public boolean isWhiteToMove() {
        return whiteToMove;
    }

}