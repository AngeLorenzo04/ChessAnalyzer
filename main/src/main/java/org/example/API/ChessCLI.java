package org.example.API;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ChessCLI {
    private final Scanner scanner = new Scanner(System.in);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public String promptUsername() {
        System.out.print("Inserisci il nome utente Chess.com: ");
        return scanner.nextLine().trim();
    }

    public ChessArchive selectArchive(List<ChessArchive> archives) {
        if (archives.isEmpty()) {
            throw new IllegalArgumentException("Nessun archivio disponibile per questo utente");
        }

        System.out.println("\nArchivi disponibili:");
        for (int i = 0; i < archives.size(); i++) {
            System.out.printf("%d) %s\n", i + 1, archives.get(i));
        }

        System.out.print("Seleziona un archivio (numero): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > archives.size()) {
            throw new IllegalArgumentException("Selezione non valida");
        }

        return archives.get(choice - 1);
    }

    public ChessGame selectGame(List<ChessGame> games) {
        System.out.println("\nPartite disponibili:");
        for (int i = 0; i < games.size(); i++) {
            String date = dateFormatter.format(Instant.ofEpochSecond(games.get(i).getTimestamp()));
            System.out.printf("%d) Partita del %s\n", i + 1, date);
        }

        System.out.print("Seleziona una partita (numero): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > games.size()) {
            throw new IllegalArgumentException("Selezione non valida");
        }

        return games.get(choice - 1);
    }

    public void displayPGN(String pgn) {
        System.out.println("\nPGN della partita selezionata:");
        System.out.println(pgn);
    }

    public void displayError(String message) {
        System.err.println("Errore: " + message);
    }
}
