package org.example.analysis;
import java.io.*;
import java.util.*;

import static org.example.utils.BoardUtils.loadFromFen;
import static org.example.utils.ChessUtils.getPieceName;
import static org.example.utils.FenUtils.getPieceAtSquare;
import static org.example.utils.FenUtils.getPlayerColor;

/**
 * Classe per analizzare partite di scacchi utilizzando il motore Stockfish.
 * Fornisce metodi per valutare posizioni, suggerire mosse ottimali e convertire tra formati.
 */
public class ChessAnalyzer {
    public static final String STOCKFISH_PATH = "src/main/stockfish/stockfish";
    public static final int ANALYSIS_DEPTH = 10;

    private OutputStreamWriter writer;
    private BufferedReader reader;

    /**
     * Avvia il processo Stockfish e inizializza i canali di comunicazione.
     * @throws IOException Se si verificano errori di I/O
     */
    public void startStockfish() throws IOException {
        // Risorse per la comunicazione con Stockfish
        Process stockfish = new ProcessBuilder(STOCKFISH_PATH).start();
        writer = new OutputStreamWriter(stockfish.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(stockfish.getInputStream()));

        // Configurazione iniziale del motore
        sendCommand("uci");
        sendCommand("isready");
    }

    /**
     * Invia un comando a Stockfish.
     * @param command Il comando da inviare
     * @throws IOException Se si verificano errori di I/O
     */
    private void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    /**
     * Estrae il punteggio dalla risposta di Stockfish.
     * @param line La riga di risposta da analizzare
     * @return Stringa contenente il punteggio (es. "cp 20")
     */

    private String extractScore(String line) {
        String[] parts = line.split(" ");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("score")) {
                return parts[i+1] + " " + parts[i+2]; // Restituisce "cp X" o "mate Y"
            }
        }
        return "N/A";
    }


    /**
     * Ottiene il FEN corrente dalla posizione di Stockfish.
     * @return Stringa FEN rappresentante la posizione corrente
     * @throws IOException Se si verificano errori di I/O
     */
    private String getCurrentFen() throws IOException {
        sendCommand("d");
        String line;
        while ((line = readLineWithTimeout()) != null) {
            if (line.startsWith("Fen: ")) {
                return line.substring(5);
            }
        }
        return "N/A";
    }

    /**
     * Classe interna per contenere i risultati dell'analisi.
     */
    public static class EvaluationResult {
        public String score;
        public String bestMove;
        public String pieceMoved;  // Pezzo mosso (es. "Pedone", "Cavallo")
        public String playerColor; // Colore del giocatore che ha mosso ("Bianco" o "Nero")
        public String sanMove;     // Mossa in notazione SAN
        public String comment;


        /**
         * Costruttore per il risultato dell'analisi.
         * @param score Valutazione della posizione
         * @param bestMove Mossa migliore suggerita
         * @param pieceMoved Pezzo mosso
         * @param playerColor Colore del giocatore
         * @param sanMove Mossa in notazione SAN
         */
        public EvaluationResult(String score, String bestMove, String pieceMoved, String playerColor, String sanMove) {
            this.score = score;
            this.bestMove = bestMove;
            this.pieceMoved = pieceMoved;
            this.playerColor = playerColor;
            this.sanMove = sanMove;
            this.comment = GenerateComment(this.score);
        }

        public String GenerateComment(String score){
            // Pulisce la stringa da eventuali spazi
            score = score.trim();

            if (score.startsWith("mate") || score.startsWith("#")) {
                // Gestione del matto
                String numero = score.replaceAll("[^0-9-]", ""); // Estrae il numero
                int mateIn = Integer.parseInt(numero);

                if (mateIn > 0) {
                    return "Matto in " + mateIn + " mosse. Vittoria forzata in vista!";
                } else {
                    return "Stai per essere matto in " + Math.abs(mateIn) + " mosse! Cerca una difesa disperata.";
                }

            } else if (score.startsWith("cp") || score.matches("[0-9.]+")) {
                // Gestione delle valutazioni centipawn
                double punteggio;
                try {
                    punteggio = Double.parseDouble(score.substring(3));
                } catch (NumberFormatException e) {
                    return "Valutazione non valida.";
                }

                if (punteggio > 3.0) {
                    return "Grande vantaggio per il Bianco.";
                } else if (punteggio > 1.0) {
                    return "Chiaro vantaggio per il Bianco.";
                } else if (punteggio > 0.3) {
                    return "Lieve vantaggio per il Bianco.";
                } else if (punteggio > -0.3) {
                    return "Posizione equilibrata.";
                } else if (punteggio > -1.0) {
                    return "Lieve vantaggio per il Nero.";
                } else if (punteggio > -3.0) {
                    return "Chiaro vantaggio per il Nero.";
                } else {
                    return "Grande vantaggio per il Nero.";
                }

            } else {
                return "Formato di valutazione non riconosciuto.";
            }
        }


        @Override
        public String toString() {
            return playerColor + " muove " + pieceMoved + " (" + sanMove + "): " +
                    "Valutazione: " + score + " | Mossa migliore: " + bestMove + " comment: " + comment;
        }
    }


    /**
     * Analizza una sequenza di mosse partendo da una posizione iniziale.
     * @param initialFen FEN della posizione iniziale
     * @param moves Lista di mosse in formato UCI
     * @return Lista di risultati dell'analisi per ogni mossa
     * @throws IOException Se si verificano errori di I/O
     */
    public List<EvaluationResult> analyzeMoves(String initialFen, List<String> moves) throws IOException {
        List<EvaluationResult> results = new ArrayList<>();
        String currentFen = initialFen;
        for (String move : moves) {
            // Verifica mossa legale
            List<String> legalMoves = getLegalMoves(currentFen);
            if (!legalMoves.contains(move)) {
                System.err.println("Mossa non riconosciuta: " + move);
                moves.set(0,"Z");
                continue;
            }

            String playerColor = getPlayerColor(currentFen);
            String pieceMoved = getPieceMoved(currentFen, move);
            String sanMove = convertUciToSan(move, currentFen);

            // Analisi posizione
            sendCommand("position fen " + currentFen);
            String bestMove = getValidBestMove(currentFen);
            String score = getPositionScore();

            results.add(new EvaluationResult(
                    score,
                    bestMove,
                    pieceMoved,
                    playerColor,
                    sanMove
            ));

            // Esegui mossa
            sendCommand("position fen " + currentFen + " moves " + move);
            currentFen = getCurrentFen();
        }
        return results;
    }

    private String getValidBestMove(String fen) throws IOException {
        List<String> legalMoves = getLegalMoves(fen);
        sendCommand("position fen " + fen);
        sendCommand("go depth " + ANALYSIS_DEPTH);

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("bestmove")) {
                String bestMove = line.split(" ")[1];
                if (legalMoves.contains(bestMove)) {
                    return bestMove;
                }
                return legalMoves.isEmpty() ? "N/A" : legalMoves.getFirst();
            }
        }
        return "N/A";
    }

    // Ottieni il pezzo mosso
    private String getPieceMoved(String fen, String uciMove) {
        if (uciMove.length() < 2) return "Sconosciuto";

        String fromSquare = uciMove.substring(0, 2);
        char pieceChar = getPieceAtSquare(fen, fromSquare);

        // Controllo speciale per pedoni
        if (pieceChar == ' ' && uciMove.length() >= 4) {
            // Potrebbe essere una promozione
            if (uciMove.length() > 4) {
                return switch (uciMove.charAt(4)) {
                    case 'q' -> "Regina";
                    case 'r' -> "Torre";
                    case 'b' -> "Alfiere";
                    case 'n' -> "Cavallo";
                    default -> "Pedone";
                };
            }
            return "Pedone";
        }
        return getPieceName(pieceChar);
    }

    // Converti mossa UCI in SAN
    private String convertUciToSan(String uciMove, String fen) {
        if (uciMove.equals("N/A")) return uciMove;

        // Gestione mosse speciali
        switch (uciMove) {
            case "e1g1", "e8g8" -> { return "O-O"; }
            case "e1c1", "e8c8" -> { return "O-O-O"; }
        }

        loadFromFen(fen);
        return new Move(uciMove).toString();
    }

    // Ottieni solo il punteggio
    private String getPositionScore() throws IOException {
        sendCommand("go depth " + ANALYSIS_DEPTH);
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("bestmove")) break;
            if (line.startsWith("info") && line.contains("score")) {
                return extractScore(line);
            }
        }
        return "N/A";
    }

    /**
     * Classe interna per rappresentare una mossa semplificata.
     */
    static class Move {
        private final String uci;

        public Move(String uci) {
            this.uci = uci;
        }

        @Override
        public String toString() {
            if (uci.length() < 4) return uci;

            // Identifica il tipo di pezzo dal carattere iniziale
            char pieceChar = uci.charAt(0);
            String destination = uci.substring(2, 4);

            if (pieceChar == ' ' || Character.toLowerCase(pieceChar) == 'p') {
                // Mosse di pedone: solo destinazione (e4)
                return destination;
            }

            // Converti il carattere del pezzo in notazione SAN
            return switch (Character.toLowerCase(pieceChar)) {
                case 'n' -> "N" + destination;
                case 'b' -> "B" + destination;
                case 'r' -> "R" + destination;
                case 'q' -> "Q" + destination;
                case 'k' -> "K" + destination;
                default -> destination;
            };
        }
    }

    public List<String> getLegalMoves(String fen) throws IOException {
        clearInputBuffer();
        sendCommand("position fen " + fen);
        sendCommand("go perft 1");

        List<String> moves = new ArrayList<>();
        String line;

        while ((line = readLineWithTimeout()) != null) {
            if (line.contains("Nodes searched")) break;

            if (line.contains(": ") && !line.startsWith("info")) {
                String move = line.split(":")[0].trim();
                moves.add(move);
            }
        }
        return moves;
    }

    private void clearInputBuffer() throws IOException {
        while (reader.ready()) {
            reader.readLine();
        }
    }

    private String readLineWithTimeout() throws IOException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 3000) {
            if (reader.ready()) {
                return reader.readLine();
            }
        }
        return null;
    }


    public static void main(String[] args) {
        ChessAnalyzer analyzer = new ChessAnalyzer();

        try {
            // Avvia il motore Stockfish
            analyzer.startStockfish();
            System.out.println("Stockfish avviato correttamente");

            // FEN iniziale (posizione standard)
            String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

            // Sequenza di mosse da analizzare (in formato UCI)
            List<String> moves = Arrays.asList(
                    "e2e4",  // Pedone bianco avanza
                    "e7e5",  // Pedone nero avanza
                    "g1f3",  // Cavallo bianco
                    "b8c6",  // Cavallo nero
                    "f1c4",  // Alfiere bianco
                    "f8c5",  // Alfiere nero
                    "e1g1",  // Arrocco corto bianco
                    "e8g8"   // Arrocco corto nero
            );

            System.out.println("Analisi della partita in corso...");
            System.out.println("----------------------------------");

            // Analizza la sequenza di mosse
            List<ChessAnalyzer.EvaluationResult> results = analyzer.analyzeMoves(initialFen, moves);

            // Stampa i risultati
            for (ChessAnalyzer.EvaluationResult result : results) {
                System.out.println(result);
                System.out.println("----------------------------------");
            }

        } catch (IOException e) {
            System.err.println("Errore durante l'analisi: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }
    }

}