package org.example.analysis;

import java.util.*;
import java.util.regex.*;

public class ConverterNotation {

    public static List<String> convertPgnToUci(String pgn) {
        List<String> uciMoves = new ArrayList<>();

        // Trova l'inizio delle mosse
        Pattern moveStart = Pattern.compile("1\\.\\s");
        Matcher matcher = moveStart.matcher(pgn);
        if (!matcher.find()) {
            return uciMoves;
        }

        String movesSection = pgn.substring(matcher.start());
        movesSection = movesSection.replaceAll("\\{\\[%[^}]*\\}", "")
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

    static class BoardState {
        private Map<String, Character> board;
        private String turn;
        private static final int[] KNIGHT_OFFSETS = {
                -2, -1, -2, 1, -1, -2, -1, 2, 1, -2, 1, 2, 2, -1, 2, 1
        };

        public BoardState() {
            turn = "w";
            initializeBoard();
        }

        private void initializeBoard() {
            board = new HashMap<>();
            // Pedoni
            for (char c = 'a'; c <= 'h'; c++) {
                board.put("" + c + "2", 'P');
                board.put("" + c + "7", 'p');
            }
            // Pezzi bianchi
            board.put("a1", 'R'); board.put("b1", 'N'); board.put("c1", 'B');
            board.put("d1", 'Q'); board.put("e1", 'K'); board.put("f1", 'B');
            board.put("g1", 'N'); board.put("h1", 'R');
            // Pezzi neri
            board.put("a8", 'r'); board.put("b8", 'n'); board.put("c8", 'b');
            board.put("d8", 'q'); board.put("e8", 'k'); board.put("f8", 'b');
            board.put("g8", 'n'); board.put("h8", 'r');
        }

        public void applyMove(String uci) {
            if (uci.length() < 4) return;

            String from = uci.substring(0, 2);
            String to = uci.substring(2, 4);
            Character piece = board.get(from);
            if (piece == null) return;

            if (uci.length() > 4) {
                char promo = uci.charAt(4);
                piece = turn.equals("w") ?
                        Character.toUpperCase(promo) :
                        Character.toLowerCase(promo);
            }

            board.remove(from);
            board.put(to, piece);
            turn = turn.equals("w") ? "b" : "w";
        }

        public String findPawnMove(String move) {
            if (move.length() < 2) return "0000";

            char destFile = move.charAt(0);
            int destRank = Character.getNumericValue(move.charAt(1));
            int direction = turn.equals("w") ? -1 : 1;
            int startRank = destRank + direction;

            // Caso base
            String candidate = "" + destFile + startRank;
            if (isPawnAt(candidate)) return candidate + destFile + destRank;

            // Doppio passo iniziale
            if (turn.equals("w") && destRank == 4) {
                candidate = "" + destFile + "2";
                if (isPawnAt(candidate)) return candidate + destFile + destRank;
            }
            else if (turn.equals("b") && destRank == 5) {
                candidate = "" + destFile + "7";
                if (isPawnAt(candidate)) return candidate + destFile + destRank;
            }

            // Catture
            if (move.length() > 2 && move.charAt(1) == 'x') {
                char sourceFile = move.charAt(0);
                candidate = "" + sourceFile + startRank;
                if (isPawnAt(candidate)) return candidate + destFile + destRank;
            }

            return "0000";
        }

        private boolean isPawnAt(String square) {
            Character piece = board.get(square);
            if (piece == null) return false;
            return (turn.equals("w") && piece == 'P') ||
                    (turn.equals("b") && piece == 'p');
        }

        public String findPieceSquare(char pieceType, String disamb, String dest) {
            char target = turn.equals("w") ? pieceType : Character.toLowerCase(pieceType);

            // Caso speciale per cavalli
            if (pieceType == 'N' || pieceType == 'n') {
                return findKnightSquare(target, dest, disamb);
            }

            // Altri pezzi
            for (String square : board.keySet()) {
                Character piece = board.get(square);
                if (piece == null || piece != target) continue;

                if (disamb.isEmpty()) {
                    return square;
                } else {
                    if (square.contains(disamb)) {
                        return square;
                    }
                }
            }
            return "00";
        }

        private String findKnightSquare(char target, String dest, String disamb) {
            List<String> candidates = new ArrayList<>();
            char destFile = dest.charAt(0);
            int destRank = Character.getNumericValue(dest.charAt(1));

            // Calcola tutte le possibili posizioni di partenza
            for (int i = 0; i < KNIGHT_OFFSETS.length; i += 2) {
                char file = (char)(destFile + KNIGHT_OFFSETS[i]);
                int rank = destRank + KNIGHT_OFFSETS[i + 1];

                if (file < 'a' || file > 'h' || rank < 1 || rank > 8) continue;

                String candidate = "" + file + rank;
                Character piece = board.get(candidate);
                if (piece != null && piece == target) {
                    candidates.add(candidate);
                }
            }

            // Filtra con disambiguazione
            if (!disamb.isEmpty()) {
                Iterator<String> it = candidates.iterator();
                while (it.hasNext()) {
                    String candidate = it.next();
                    if (!candidate.contains(disamb)) {
                        it.remove();
                    }
                }
            }

            if (!candidates.isEmpty()) {
                return candidates.get(0);
            }
            return "00";
        }
    }

    public static void main(String[] args) {
        String pgn = "[Event \"Live Chess\"]\n" +
                "[Site \"Chess.com\"]\n" +
                "[Date \"2021.04.05\"]\n" +
                "[Round \"-\"]\n" +
                "[White \"y7876\"]\n" +
                "[Black \"francesco_nutile\"]\n" +
                "[Result \"0-1\"]\n" +
                "[CurrentPosition \"rnbqkb1r/pppp1ppp/8/4p3/2P3nP/8/PP1PPP2/RNBQKBNR w KQkq -\"]\n" +
                "[Timezone \"UTC\"]\n" +
                "[ECO \"A20\"]\n" +
                "[ECOUrl \"https://www.chess.com/openings/English-Opening-Kings-English-Variation\"]\n" +
                "[UTCDate \"2021.04.05\"]\n" +
                "[UTCTime \"10:38:18\"]\n" +
                "[WhiteElo \"638\"]\n" +
                "[BlackElo \"962\"]\n" +
                "[TimeControl \"600\"]\n" +
                "[Termination \"francesco_nutile won by resignation\"]\n" +
                "[StartTime \"10:38:18\"]\n" +
                "[EndDate \"2021.04.05\"]\n" +
                "[EndTime \"10:44:08\"]\n" +
                "[Link \"https://www.chess.com/game/live/11358199965\"]\n\n" +
                "1. c4 {[%clk 0:09:44]} 1... e5 {[%clk 0:09:58]} 2. g4 {[%clk 0:09:37.7]} 2... Nf6 {[%clk 0:09:54.3]} " +
                "3. h4 {[%clk 0:09:15.1]} 3... Nxg4 {[%clk 0:09:51.3]} 0-1";

        List<String> uciMoves = convertPgnToUci(pgn);
        System.out.println(uciMoves);
    }
}