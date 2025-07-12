package org.example.analysis;

import java.io.*;
import java.util.*;

public class StockfishEngine implements AutoCloseable {
    private final Process process;
    private final BufferedWriter writer;
    private final BufferedReader reader;

    public StockfishEngine(String stockfishPath) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(stockfishPath);
        pb.redirectErrorStream(true);
        this.process = pb.start();

        this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    public void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    public String readUntil(String keyword) throws IOException {
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
            if (line.contains(keyword)) break;
        }
        return output.toString();
    }

    @Override
    public void close() throws IOException {
        sendCommand("quit");
        process.destroy();
    }

    /**
     * Estrae solo le mosse valide dal campo PGN
     */
    public static List<String> extractMovesFromPGN(String pgn) {
        List<String> moves = new ArrayList<>();

        String cleaned = pgn.replaceAll("\\[.*\n", "")
                .replaceAll("\\{.*?\\}", "")
                .replaceAll("\\$.*?\\$", "")
                .replaceAll("(\\s)+", " ")
                .trim();

        String[] tokens = cleaned.split(" ");
        for (String token : tokens) {
            if (!token.isEmpty() && !token.matches("\\d+\\."))
                moves.add(token.replace("...", "").replace("+", ""));
        }

        return moves;
    }

    /**
     * Imposta la posizione completa usando initial_setup + lista mosse
     */
    public void setPosition(List<String> allMoves, String initialSetup) throws IOException {
        StringBuilder command = new StringBuilder("position fen ").append(initialSetup).append(" moves");

        for (String move : allMoves) {
            String cleanedMove = move.toLowerCase().replaceAll("[^a-z0-9\\-]", "");
            if (!cleanedMove.isEmpty()) {
                command.append(" ").append(cleanedMove);
            }
        }

        sendCommand(command.toString());
    }

    /**
     * Chiede l'analisi al motore
     */
    public String analyzePosition(int depth) throws IOException {
        sendCommand("go depth " + depth);
        return readUntil("bestmove");
    }
}