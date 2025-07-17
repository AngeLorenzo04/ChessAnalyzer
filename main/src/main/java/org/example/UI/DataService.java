package org.example.UI;// DataService.java
import org.example.API.ChessAPIService;
import org.example.API.ChessArchive;
import org.example.API.ChessDataParser;
import org.example.API.ChessGame;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DataService {


    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    public static List<ChessArchive> getArchivesForUser(String username) throws IOException, InterruptedException {
        // Implementazione reale con chiamata API
        ChessAPIService apiService = new ChessAPIService();
        ChessDataParser dataParser = new ChessDataParser();
        String archivesJason = apiService.getGameArchives(username);
        return dataParser.parseArchives(archivesJason);
    }

    public static List<ChessGame> getGamesForArchive(ChessArchive archive) throws IOException, InterruptedException {
        ChessAPIService apiService = new ChessAPIService();
        ChessDataParser dataParser = new ChessDataParser();
        String gamesJson = apiService.getGamesFromArchive(archive.getUrl());
        return dataParser.parseGames(gamesJson);
    }

    public static void printGameInfo(String username, ChessArchive archive, ChessGame game) {
        String date = DATE_FORMATTER.format(Instant.ofEpochSecond(game.getTimestamp()));
        String message = String.format(
                """
                        Partita selezionata:
                        Giocatore: %s
                        Archivio: %s
                        Data: %s""",
                username, archive, date
        );
        System.out.println(message);
    }
}