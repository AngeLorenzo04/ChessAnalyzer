// File: Board.java
package org.example.utils;


public class BoardUtils {
    private static final char[][] squares = new char[8][8];
    /**
     * Carica una posizione dalla notazione FEN.
     *
     * @param fen La stringa FEN da caricare
     */
    public static void loadFromFen(String fen) {
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
    }
}