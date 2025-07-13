package org.example.analysis;

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
    /**
     * Inizializza una nuova scacchiera con la posizione iniziale standard
     */
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
        /**
         * Applica una mossa UCI alla scacchiera
         * @param uci La mossa in formato UCI da applicare
         */
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
        /**
         * Trova la mossa UCI corrispondente a una mossa di pedone
         * @param move La mossa in notazione algebrica
         * @return La mossa in formato UCI
         */
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
        /**
         * Trova la posizione di partenza di un pezzo
         * @param pieceType Il tipo di pezzo (es. 'N' per cavallo)
         * @param disamb Stringa di disambiguazione (file o rank)
         * @param dest Casella di destinazione
         * @return La casella di partenza del pezzo
         */
        public String findPieceSquare(char pieceType, String disamb, String dest) {
            char target = turn.equals("w") ? pieceType : Character.toLowerCase(pieceType);

            // Caso speciale per cavalli
            if (pieceType == 'N' || pieceType == 'n') {
                return findKnightSquare(target, dest, disamb);
            }

            // Lista di possibili candidati
            List<String> candidates = new ArrayList<>();

            // Cerca tutti i pezzi dello stesso tipo
            for (String square : board.keySet()) {
                Character piece = board.get(square);
                if (piece != null && piece == target) {
                    candidates.add(square);
                }
            }

            // Se c'è solo un candidato, restituiscilo
            if (candidates.size() == 1) {
                return candidates.get(0);
            }

            // Altrimenti, filtra per disambiguazione
            if (!disamb.isEmpty()) {
                Iterator<String> it = candidates.iterator();
                while (it.hasNext()) {
                    String candidate = it.next();
                    if (!candidate.contains(disamb)) {
                        it.remove();
                    }
                }
            }

            // Se ci sono ancora più candidati, scegli quello che può raggiungere la destinazione
            if (!candidates.isEmpty()) {
                for (String candidate : candidates) {
                    if (canReach(candidate, dest, pieceType)) {
                        return candidate;
                    }
                }
            }

            return "00";
        }

        private boolean canReach(String from, String to, char pieceType) {
            char fileFrom = from.charAt(0);
            int rankFrom = Character.getNumericValue(from.charAt(1));
            char fileTo = to.charAt(0);
            int rankTo = Character.getNumericValue(to.charAt(1));

            int fileDiff = fileTo - fileFrom;
            int rankDiff = rankTo - rankFrom;

            // Alfieri si muovono in diagonale
            if (Character.toUpperCase(pieceType) == 'B') {
                return Math.abs(fileDiff) == Math.abs(rankDiff);
            }
            // Torri si muovono in linea retta
            else if (Character.toUpperCase(pieceType) == 'R') {
                return (fileDiff == 0 || rankDiff == 0);
            }
            // Regine si muovono come alfiere o torre
            else if (Character.toUpperCase(pieceType) == 'Q') {
                return (Math.abs(fileDiff) == Math.abs(rankDiff)) ||
                        (fileDiff == 0 || rankDiff == 0);
            }

            return false;
        }

        /**
         * Trova la posizione di partenza di un cavallo
         * @param target Il tipo di cavallo ('N' o 'n')
         * @param dest Casella di destinazione
         * @param disamb Stringa di disambiguazione
         * @return La casella di partenza del cavallo
         */
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
                "[Date \"2025.06.19\"]\n" +
                "[Round \"-\"]\n" +
                "[White \"y7876\"]\n" +
                "[Black \"Kazzakh2\"]\n" +
                "[Result \"1-0\"]\n" +
                "[WhiteElo \"357\"]\n" +
                "[BlackElo \"335\"]\n" +
                "[TimeControl \"600\"]\n" +
                "[EndTime \"12:12:27 GMT+0000\"]\n" +
                "[Termination \"y7876 ha vinto per abbandono\"]\n" +
                "\n" +
                "1. d4 d5 2. Nc3 Nf6 3. Nf3 e6 4. Bf4 Bd6 5. g3 Ne4 6. Nb5 c6 7. Nxd6+ 1-0";

        List<String> uciMoves = convertPgnToUci(pgn);
        System.out.println(uciMoves);
    }
}