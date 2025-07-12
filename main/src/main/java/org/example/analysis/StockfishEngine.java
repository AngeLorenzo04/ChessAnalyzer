package org.example.analysis;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe per comunicare con il motore scacchistico Stockfish.
 * Permette di:
 * - Avviare il processo Stockfish
 * - Inviare comandi (posizioni, analisi)
 * - Leggere l'output (valutazioni, mosse migliori)
 */
public class StockfishEngine implements AutoCloseable {
    private final Process process;
    private final BufferedWriter writer;
    private final BufferedReader reader;

    /**
     * Avvia Stockfish come processo esterno
     *
     * @param stockfishPath Percorso dell’eseguibile Stockfish
     * @throws IOException Se si verifica un errore nell’avvio
     */
    public StockfishEngine(String stockfishPath) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(stockfishPath);
        pb.redirectErrorStream(true); // Unisci output ed error stream
        this.process = pb.start();

        // Configura input/output per comunicare con Stockfish
        this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    /**
     * Invia un comando a Stockfish
     */
    public void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    /**
     * Legge l'output finché non trova la parola chiave
     */
    public String readUntil(String keyword) throws IOException {
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
            if (line.contains(keyword)) break; // Interrompi quando trovi la parola chiave
        }
        return output.toString();
    }

    /**
     * Imposta una posizione iniziale dal PGN della partita
     */
    public void setPositionFromPGN(String pgn) throws IOException {
        List<String> moves = extractMovesFromPGN(pgn);
        StringBuilder moveLine = new StringBuilder("position startpos moves");

        for (String move : moves) {
            // Pulisce la mossa (rimuove simboli extra)
            moveLine.append(" ").append(move.toLowerCase().replace("x", "").replace("+", ""));
        }

        sendCommand(moveLine.toString());
    }

    /**
     * Chiede a Stockfish di analizzare la posizione corrente
     *
     * @param depth Profondità dell’analisi (es. 10)
     * @return Output completo del motore
     * @throws IOException Se c'è un problema nella comunicazione
     */
    public String analyzePosition(int depth) throws IOException {
        sendCommand("go depth " + depth);
        return readUntil("bestmove");
    }

    /**
     * Estrae solo le mosse dal campo PGN
     *
     * @param pgn Campo PGN della partita
     * @return Lista di mosse giocate (es. ["b3", "g6", ...])
     */
    public static List<String> extractMovesFromPGN(String pgn) {
        List<String> moves = new ArrayList<>();

        // Rimuove l'intestazione tra parentesi quadre
        String[] lines = pgn.split("\n");
        boolean inMoves = false;

        for (String line : lines) {
            line = line.trim();

            // Ignora le righe di intestazione
            if (line.startsWith("[")) continue;

            // Le righe successive sono le mosse
            if (!line.isEmpty()) {
                inMoves = true;
                String[] parts = line.split("\\s+");

                for (String part : parts) {
                    // Esclude numeri di mossa (es. "1." o "37...") e simboli come "{" o "}"
                    if (part.matches("[a-zA-Z0-9\\-\\=]+") && !part.matches("\\d+\\."))
                        moves.add(part.replace("...", "").replace("+", ""));
                }
            }
        }

        return moves;
    }

    /**
     * Chiude il processo Stockfish alla fine
     */
    @Override
    public void close() throws IOException {
        sendCommand("quit"); // Manda il comando di chiusura
        process.destroy();   // Termina il processo
    }
}