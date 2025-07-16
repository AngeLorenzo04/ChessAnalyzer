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
        String pgn = "1. e4 e6 2. Nf3 d5 3. exd5 exd5 4. d4 Nf6 5. Bg5 Be7 6. c4 c5 7. cxd5 cxd4 8. Qxd4 Nxd5 9. Qxg7 Rf8 10. Bxe7 Nxe7 11. Bb5+ Bd7 12. Bxd7+ Nxd7 13. Nc3 Nf5 14. Qxh7 Qe7+ 15. Ne2 O-O-O 16. O-O Rh8 17. Qxf5 Kb8 18. Nc3 f6 19. Nd5 Qh7 20. Qf4+Ne5 21. Nxf6 Qh6 22. Qxe5+ Ka8 23. Rfd1 Rdf8 24. Ng4 Qb6 25. b3 a5 26. Rd6 Re827. Rxb6 Rxe5 28. Nfxe5 Ka7 29. Rb5 Ka6 30. Rc5 Kb6 31. Rac1 Rd8 32. Nc4+ Kxc533. Nxa5+ Kb4 34. Nxb7 Rd7 35. Nc5 Rc7 36. Nd3+ Ka3 37. Rxc7 Kxa2 38. h4 Kxb3 39. h5 1-0";

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