package org.example.API;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ChessGame(String pgn) {

    public static String formatPlayersAndDate(String inputText) {
        // Estrae i valori dei tag White, Black e Date usando regex
        String white = extractTagValue(inputText, "White");
        String black = extractTagValue(inputText, "Black");
        String date = extractTagValue(inputText, "Date");

        // Formatta la stringa come "neroVSbianco-data" (es: y7876VSrated_r_rookie-2023.11.14)
        return String.format("%s-VS-%s data: %s", black, white, date);
    }

    // Helper: Estrae il valore di un tag PGN (es: [White "Player"] -> "Player")
    private static String extractTagValue(String text, String tag) {
        Pattern pattern = Pattern.compile("\\[" + tag + " \"(.*?)\"]");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : "?";
    }

    @Override
    public String toString() {
        return formatPlayersAndDate(pgn);
    }
}
