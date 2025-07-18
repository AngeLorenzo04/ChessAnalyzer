package org.example.API;

import java.util.List;

import com.google.gson.annotations.SerializedName;

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

    GamesResponse(List<GameData> gameData) {
        this.gameData = gameData;
    }

    public List<ChessGame> getGames() {
        return gameData.stream()
                .map(data -> new ChessGame(data.pgn))
                .toList();
    }

    private static class GameData {
        @SerializedName("pgn")
        String pgn;
    }
}

