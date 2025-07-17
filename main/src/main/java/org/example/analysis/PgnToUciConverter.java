package org.example.analysis;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PgnToUciConverter {
    public static List<String> convertPgnToUci(String pgn) throws IOException, InterruptedException {
        // 1. Crea file temporanei con prefisso identificativo
        File inputFile = File.createTempFile("chess_input_", ".pgn");
        File outputFile = File.createTempFile("chess_output_", ".uci");

        // 2. Scrivi il PGN nel file di input
        Files.writeString(inputFile.toPath(), pgn);
        System.out.println("[DEBUG] Input file: " + inputFile.getAbsolutePath());
        System.out.println("[DEBUG] Output file: " + outputFile.getAbsolutePath());

        // 3. Configura il processo con timeout
        ProcessBuilder pb = new ProcessBuilder(
                "pgn-extract",
                "-s",
                "-Wuci",
                "-o", outputFile.getAbsolutePath(),
                inputFile.getAbsolutePath()
        );

        // 4. Reindirizza error stream per debug
        pb.redirectErrorStream(true);

        // 5. Esegui il processo con timeout
        Process process = pb.start();
        boolean finished = process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);

        if (!finished) {
            process.destroy();
            throw new IOException("pgn-extract timeout dopo 10 secondi");
        }

        // 6. Controlla exit code
        int exitCode = process.exitValue();
        System.out.println("[DEBUG] Exit code: " + exitCode);

        // 7. Leggi l'output di debug
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            System.out.println("[DEBUG] pgn-extract output:");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("  > " + line);
            }
        }

        // 8. Verifica esistenza file di output
        if (!outputFile.exists() || outputFile.length() == 0) {
            throw new IOException("File di output non generato");
        }

        // 9. Processa il risultato
        List<String> allLines = Files.readAllLines(outputFile.toPath(), StandardCharsets.UTF_8);
        List<String> moves = new ArrayList<>();

        for (String line : allLines) {
            // Salta linee di commento/metadata
            if (!line.startsWith("[") && !line.trim().isEmpty()) {
                String[] tokens = line.trim().split("\\s+");
                for (String token : tokens) {
                    if (isValidUciMove(token)) {
                        moves.add(token);
                    }
                }
            }
        }

        System.out.println("[DEBUG] Mosse trovate: " + moves.size());
        return moves;
    }

    private static boolean isValidUciMove(String move) {
        return move.matches("[a-h][1-8][a-h][1-8][qrnb]?") &&
                !move.matches("1-0|0-1|1/2-1/2");
    }

    public static void main(String[] args) {
        String pgn  = "1. e4 1... d5 2. d3 2... e6 3. d4 3... Bb4+ 4. Bd2 4... Qg5 5. Bb5+ 5... c6 6. Bxb4 6... Nf6 7. Bd2 7... dxe4 8. Bxg5 8... h6 9. Bxf6 9... gxf6 10. Nc3 10... e5 11. Nxe4 11... exd4 12. Qxd4 12... f5 13. Nf6+ 13... Ke7 14. Nh5 14... Rd8 15. Qf6+ 15... Kf8 16. Qxd8# 1-0";
        try {
            List<String> uciMoves = PgnToUciConverter.convertPgnToUci(pgn);
            System.out.println("Mosse convertite (" + uciMoves.size() + "):");
            for (String move : uciMoves) {
                System.out.println(move);
            }
        } catch (Exception ignored) {

        }
    }
}