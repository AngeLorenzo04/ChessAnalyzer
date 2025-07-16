package org.example.API;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChessAPIService {
    private static final String BASE_URL = "https://api.chess.com/pub/player/";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String getGameArchives(String username) throws IOException, InterruptedException {
        String apiUrl = BASE_URL + username + "/games/archives";
        return sendGetRequest(apiUrl);
    }

    public String getGamesFromArchive(String archiveUrl) throws IOException, InterruptedException {
        return sendGetRequest(archiveUrl);
    }

    private String sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("HTTP error " + response.statusCode() + ": " + response.body());
        }

        return response.body();
    }
}
