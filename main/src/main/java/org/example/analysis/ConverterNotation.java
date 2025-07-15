package org.example.analysis;

import org.example.utils.BoardState;

import java.util.*;
import java.util.regex.*;

public class ConverterNotation {
    /**
     * Converte una partita in formato PGN in una lista di mosse UCI
     * @param pgn La partita in formato PGN da convertire
     * @return Lista di mosse in formato UCI
     */
    public static List<String> convertPgnToUci(String pgn) {
        List<String> uciMoves = new ArrayList<>();

        // Trova l'inizio delle mosse
        Pattern moveStart = Pattern.compile("1\\.\\s");
        Matcher matcher = moveStart.matcher(pgn);
        if (!matcher.find()) {
            return uciMoves;
        }

        String movesSection = pgn.substring(matcher.start());
        movesSection = movesSection.replaceAll("\\{\\[%[^}]*}", "")
                .replaceAll("\\d+\\.\\.\\.", " ")
                .replaceAll("\\d+\\.", " ")
                .replaceAll("\\s+", " ")
                .trim();

        String[] moveTokens = movesSection.split("\\s+");
        BoardState state = new BoardState();

        for (String token : moveTokens) {
            if (token.isEmpty() || token.matches("1-0|0-1|1/2-1/2")) {
                continue;
            }

            String cleanToken = token.replaceAll("[+#x!?]", "").trim();
            if (cleanToken.isEmpty()) continue;

            // Gestione arrocco
            if (cleanToken.matches("O-O(-O)?")) {
                String move = state.turn.equals("w") ?
                        (cleanToken.contains("-O-O") ? "e1c1" : "e1g1") :
                        (cleanToken.contains("-O-O") ? "e8c8" : "e8g8");
                uciMoves.add(move);
                state.applyMove(move);
                continue;
            }

            // Gestione promozioni
            String move = cleanToken;
            String promotion = "";
            if (cleanToken.contains("=")) {
                String[] parts = cleanToken.split("=");
                move = parts[0];
                promotion = parts.length > 1 ? parts[1].substring(0, 1).toLowerCase() : "";
            }

            // Conversione notazione algebrica
            String uciMove = convertAlgebraicMove(move, state);
            if (uciMove == null || uciMove.length() < 4) continue;

            if (!promotion.isEmpty()) uciMove += promotion;
            uciMoves.add(uciMove);
            state.applyMove(uciMove);
        }

        return uciMoves;
    }
    /**
     * Converte una mossa in notazione algebrica in formato UCI
     * @param move La mossa in notazione algebrica
     * @param state Lo stato corrente della scacchiera
     * @return La mossa in formato UCI, o null se la conversione fallisce
     */
    private static String convertAlgebraicMove(String move, BoardState state) {
        if (move.length() < 2) return null;

        if (Character.isLowerCase(move.charAt(0))) {
            return state.findPawnMove(move);
        } else {
            char pieceType = move.charAt(0);
            String dest = move.substring(move.length() - 2);
            String disamb = move.length() > 3 ? move.substring(1, move.length() - 2) : "";
            return state.findPieceSquare(pieceType, disamb, dest) + dest;
        }
    }
}