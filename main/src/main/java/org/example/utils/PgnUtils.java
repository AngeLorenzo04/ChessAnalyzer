package org.example.utils;

public class PgnUtils {

    /**
     * Pulisce il PGN per ottenere il formato compatto richiesto
     * @param pgn PGN grezzo da pulire
     * @return PGN formattato con mosse compatte
     */
    public static String cleanForCompactFormat(String pgn) {
        // 1. Rimuovi tutti i tag
        if (pgn == null || pgn.isEmpty()) return "";

        // 1. Rimuove commenti tra graffe
        String pulito = pgn.replaceAll("\\{[^}]*\\}", "");

        // 2. Rimuove metadati tra parentesi quadre
        pulito = pulito.replaceAll("\\[[^\\]]*\\]", "");

        // 5. Rimuove doppi spazi e trim finale
        pulito = pulito.replaceAll("\\s+", " ").trim();

        return pulito;
    }
}
