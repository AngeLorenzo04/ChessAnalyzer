package org.example.utils;

public class PgnUtils {

    /**
     * Pulisce il PGN per ottenere il formato compatto richiesto
     * @param rawPgn PGN grezzo da pulire
     * @return PGN formattato con mosse compatte
     */
    public static String cleanForCompactFormat(String rawPgn) {
        // 1. Rimuovi tutti i tag
        String cleaned = rawPgn.replaceAll("\\[.*?\\]", "");
        // 2. Rimuovi tutte le annotazioni temporali e i commenti
        cleaned = cleaned.replaceAll("\\{\\[%clk [^}]*\\}", "");
        cleaned = cleaned.replaceAll("\\{[^}]*\\}", "");
        // 3. Normalizza gli spazi e le mosse
        cleaned = cleaned
                .replaceAll("\\d+\\.\\.\\.", " ")       // Rimuovi numeri turno nero
                .replaceAll("\\d+\\.", "")               // Rimuovi numeri turno bianco
                .replaceAll("\\s+", " ")                 // Normalizza spazi
                .replaceAll("\\s0-0-0\\s", " O-O-O ")    // Arrocco lungo
                .replaceAll("\\s0-0\\s", " O-O ")        // Arrocco corto
                .replaceAll("\\s1/2-1/2\\s", " 1/2-1/2") // Patta
                .replaceAll("\\s1-0\\s", " 1-0")         // Vittoria bianco
                .replaceAll("\\s0-1\\s", " 0-1")         // Vittoria nero
                .trim();
        // 4. Formattazione finale con numerazione compatta
        return compactMoveNumbers(cleaned);
    }
    /**
     * Raggruppa le mosse in gruppi di 6 per riga con numerazione
     * @param moves Stringa di mosse pulite
     * @return Stringa formattata con numerazione compatta
     */
    private static String compactMoveNumbers(String moves) {
        String[] tokens = moves.split(" ");
        StringBuilder result = new StringBuilder();
        int moveCount = 0;
        int lineCount = 0;
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            // Gestione risultato finale
            if (token.matches("1-0|0-1|1/2-1/2")) {
                result.append(token);
                break;
            }
            // Nuovo gruppo di mosse
            if (moveCount == 0) {
                lineCount++;
                result.append(lineCount).append(". ");
            }
            result.append(token);
            // Gestione spaziatura
            if (i < tokens.length - 1) {
                // Determina se aggiungere spazio o andare a capo
                if (moveCount < 5) {
                    result.append(" ");
                    moveCount++;
                } else {
                    result.append("\n");
                    moveCount = 0;
                }
            }
        }
        return result.toString();
    }
}
