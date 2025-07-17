package org.example.API;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.annotations.SerializedName;

public class ApiUtils {

    public static void main(String[] args) {
        ChessAPIService apiService = new ChessAPIService();
        ChessDataParser dataParser = new ChessDataParser();
        ChessCLI cli = new ChessCLI();

        try {
            CompletableFuture<ChessArchive> selectedArchive = cli.selectArchive();
            String gamesJson = apiService.getGamesFromArchive(selectedArchive.get().getUrl());
            List<ChessGame> games = dataParser.parseGames(gamesJson);

            cli.displayPGN();
        } catch (Exception e) {
            cli.displayError();
        }
    }
}

// Modelli dati per il parsing JSON
class ArchivesResponse {
    @SerializedName("archives")
    private List<String> urls;

    public List<ChessArchive> getArchives() {
        return urls.stream().map(ChessArchive::new).toList();
    }
}

class GamesResponse {
    @SerializedName("games")
    private List<GameData> gameData;

    public List<ChessGame> getGames() {
        return gameData.stream()
                .map(data -> new ChessGame(data.pgn, data.end_time))
                .toList();
    }

    private static class GameData {
        @SerializedName("pgn")
        String pgn;
        @SerializedName("end_time")
        long end_time;
    }
}

