package org.example.API;

import com.google.gson.Gson;

import java.util.List;

public class ChessDataParser {
    private final Gson gson = new Gson();

    public List<ChessArchive> parseArchives(String json) {
        ArchivesResponse response = gson.fromJson(json, ArchivesResponse.class);
        return response.getArchives();
    }

    public List<ChessGame> parseGames(String json) {
        GamesResponse response = gson.fromJson(json, GamesResponse.class);
        return response.getGames();
    }
}
