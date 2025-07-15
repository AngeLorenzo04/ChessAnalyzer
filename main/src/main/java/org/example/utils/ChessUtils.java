// File: ChessUtils.java
package org.example.utils;

/**
 * Classe di utilità per operazioni comuni sugli scacchi.
 * Fornisce metodi statici per la conversione dei nomi dei pezzi e altre operazioni.
 */
public class ChessUtils {

    /**
     * Converte un carattere di pezzo nel nome corrispondente.
     *
     * @param pieceChar Il carattere che rappresenta il pezzo (es. 'p', 'N')
     * @return Il nome del pezzo in italiano
     */
    public static String getPieceName(char pieceChar) {
        return switch (Character.toLowerCase(pieceChar)) {
            case 'p' -> "Pedone";
            case 'n' -> "Cavallo";
            case 'b' -> "Alfiere";
            case 'r' -> "Torre";
            case 'q' -> "Regina";
            case 'k' -> "Re";
            default -> "Sconosciuto";
        };
    }
}