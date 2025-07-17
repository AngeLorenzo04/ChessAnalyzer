package org.example.UI;// DataService.java
import org.example.API.ChessArchive;
import org.example.API.ChessGame;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DataService {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    public static List<ChessArchive> getArchivesForUser(String username) {
        // Implementazione reale con chiamata API
        return List.of(
                new ChessArchive("https://api.chess.com/pub/player/" + username + "/games/2023/10"),
                new ChessArchive("https://api.chess.com/pub/player/" + username + "/games/2023/09")
        );
    }

    public static List<ChessGame> getGamesForArchive(ChessArchive archive) {
        // Implementazione reale con chiamata API
        long baseTimestamp = System.currentTimeMillis() / 1000;
        return List.of(
                new ChessGame("PGN content 1", baseTimestamp - 86400),
                new ChessGame("PGN content 2", baseTimestamp - 172800)
        );
    }

    public static void printGameInfo(String username, ChessArchive archive, ChessGame game) {
        String date = DATE_FORMATTER.format(Instant.ofEpochSecond(game.getTimestamp()));
        String message = String.format(
                "Partita selezionata:\n" +
                        "Giocatore: %s\n" +
                        "Archivio: %s\n" +
                        "Data: %s",
                username, archive, date
        );
        System.out.println(message);
    }
}