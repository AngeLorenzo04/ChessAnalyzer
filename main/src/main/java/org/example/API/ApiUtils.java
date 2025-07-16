package org.example.API;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ApiUtils {

    public static void main(String[] args) {
        ChessAPIService apiService = new ChessAPIService();
        ChessDataParser dataParser = new ChessDataParser();
        ChessCLI cli = new ChessCLI();

        try {
            String username = cli.promptUsername();
            String archivesJson = apiService.getGameArchives(username);
            List<ChessArchive> archives = dataParser.parseArchives(archivesJson);

            ChessArchive selectedArchive = cli.selectArchive(archives);
            String gamesJson = apiService.getGamesFromArchive(selectedArchive.getUrl());
            List<ChessGame> games = dataParser.parseGames(gamesJson);

            ChessGame selectedGame = cli.selectGame(games);
            cli.displayPGN(selectedGame.getPgn());
        } catch (Exception e) {
            cli.displayError(e.getMessage());
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

