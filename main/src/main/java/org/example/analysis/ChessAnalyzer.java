package org.example.analysis;


import java.io.*;
import java.util.*;

/**
 * Classe per analizzare partite di scacchi utilizzando il motore Stockfish.
 * Fornisce metodi per valutare posizioni, suggerire mosse ottimali e convertire tra formati.
 */
public class ChessAnalyzer {
    public static final String STOCKFISH_PATH = "main/src/main/stockfish/stockfish";
    public static final int ANALYSIS_DEPTH = 20;

    // Risorse per la comunicazione con Stockfish
    private Process stockfish;
    private OutputStreamWriter writer;
    private BufferedReader reader;

    /**
     * Avvia il processo Stockfish e inizializza i canali di comunicazione.
     * @throws IOException Se si verificano errori di I/O
     */
    public void startStockfish() throws IOException {
        stockfish = new ProcessBuilder(STOCKFISH_PATH).start();
        writer = new OutputStreamWriter(stockfish.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(stockfish.getInputStream()));

        // Configurazione iniziale del motore
        sendCommand("uci");
        sendCommand("isready");
    }

    /**
     * Termina correttamente il processo Stockfish.
     * @throws IOException Se si verificano errori di I/O
     */
    public void stopStockfish() throws IOException {
        sendCommand("quit");
        stockfish.destroy();
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
        sendCommand("d"); //comando che permette di ottenere la situazione attuale

        //Ricavo lo stato della scacchiera leggendo le righe che il comando 'd' ha generato
        String line;
        while ((line = reader.readLine()) != null) {
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
        }

        @Override
        public String toString() {
            return playerColor + " muove " + pieceMoved + " (" + sanMove + "): " +
                    "Valutazione: " + score + " | Mossa migliore: " + bestMove;
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
            // Determina il colore del giocatore
            String playerColor = getPlayerColor(currentFen);

            // Ottieni il pezzo mosso e la mossa SAN
            String pieceMoved = getPieceMoved(currentFen, move);
            String sanMove = convertUciToSan(move, currentFen);

            // Imposta la posizione corrente
            sendCommand("position fen " + currentFen);

            // Ottieni valutazione e mossa migliore
            String bestMove = getBestMove();
            String score = getPositionScore();

            // Crea il risultato
            EvaluationResult result = new EvaluationResult(
                    score,
                    convertUciToSan(bestMove, currentFen),
                    pieceMoved,
                    playerColor,
                    sanMove
            );

            results.add(result);

            // Esegui la mossa
            sendCommand("position fen " + currentFen + " moves " + move);
            currentFen = getCurrentFen();
        }

        return results;
    }

    // Determina il colore del giocatore
    private String getPlayerColor(String fen) {
        // Il secondo campo nel FEN indica il turno
        String[] parts = fen.split(" ");
        return parts[1].equals("w") ? "Bianco" : "Nero";
    }

    // Ottieni il carattere del pezzo in una casella
    private char getPieceAtSquare(String fen, String square) {
        // Estrai la parte della posizione dal FEN (prima parte prima dello spazio)
        String positionPart = fen.split(" ")[0];
        String[] rows = positionPart.split("/");

        // Converti la notazione della casella (es. "e4") in indici di riga e colonna
        int col = square.charAt(0) - 'a'; // 0-7
        int row = 8 - Character.getNumericValue(square.charAt(1)); // 0-7

        // Scorri la riga per trovare il pezzo
        String fenRow = rows[row];
        int currentCol = 0;

        for (char c : fenRow.toCharArray()) {
            if (Character.isDigit(c)) {
                // Se è un numero, salta quelle caselle vuote
                currentCol += Character.getNumericValue(c);
            } else {
                if (currentCol == col) {
                    return c; // Trovato il pezzo
                }
                currentCol++;
            }
        }

        return ' '; // Casella vuota
    }

    // Converti carattere pezzo in nome
    private static String getPieceName(char pieceChar) {
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

    // Ottieni il pezzo mosso
    private String getPieceMoved(String fen, String uciMove) {
        // Estrai la casella di partenza (primi 2 caratteri)
        String fromSquare = uciMove.substring(0, 2);

        // Ottieni il carattere del pezzo dalla posizione
        char pieceChar = getPieceAtSquare(fen, fromSquare);

        // Converti in nome del pezzo
        return getPieceName(pieceChar);
    }

    // Converti mossa UCI in SAN
    private String convertUciToSan(String uciMove, String fen) {
        Board board = new Board();
        board.loadFromFen(fen);
        Move move = new Move(uciMove, board.getSideToMove());
        return move.toString();
    }

    // Ottieni solo la mossa migliore
    private String getBestMove() throws IOException {
        sendCommand("go depth " + Integer.toString(ANALYSIS_DEPTH));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("bestmove")) {
                return extractBestMove(line);
            }
        }
        return "N/A";
    }


    // Ottieni solo il punteggio
    private String getPositionScore() throws IOException {
        sendCommand("go depth " + Integer.toString(ANALYSIS_DEPTH));
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
     * Classe interna per rappresentare una scacchiera semplificata.
     * (Nota: nella pratica sarebbe meglio usare una libreria dedicata)
     */
    static class Board {
        private String fen;
        private final char[][] squares = new char[8][8];
        private boolean whiteToMove;

        public void loadFromFen(String fen) {
            this.fen = fen;
            String[] parts = fen.split(" ");
            String[] rows = parts[0].split("/");

            // Inizializza la scacchiera
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
         * Ottiene il pezzo in una specifica casella.
         * @param square Casella in notazione algebrica (es. "e4")
         * @return Carattere rappresentante il pezzo
         */
        public char getPieceAt(String square) {
            int col = square.charAt(0) - 'a';
            int row = 8 - Character.getNumericValue(square.charAt(1));
            return squares[row][col];
        }

        public boolean getSideToMove() {
            return whiteToMove;
        }
    }

    /**
     * Classe interna per rappresentare una mossa semplificata.
     */
    static class Move {
        private final String uci;
        private boolean whiteMove;

        public Move(String uci, boolean whiteMove) {
            this.uci = uci;
            this.whiteMove = whiteMove;
        }

        @Override
        public String toString() {
            // Implementazione base - nella realtà usare una libreria
            String piece = getPieceName(uci.charAt(0));
            return piece.charAt(0) + uci.substring(2, 4);
        }
    }


    // Estrai la mossa migliore
    private String extractBestMove(String line) {
        String[] parts = line.split(" ");
        if (parts.length >= 2) {
            return parts[1];  // Formato UCI (es. "e2e4")
        }
        return "N/A";
    }

}