package org.example.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.model.Game;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ChessComApiClient {

    private static final String BASE_URL = "https://api.chess.com/pub/player/";

    /**
     * Scarica gli archivi mensili dell'utente
     */
    public static List<String> getPlayerMonthlyArchives(String username) throws Exception {
        String url = BASE_URL + username + "/games/archives";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Errore nella chiamata API: " + response.statusCode());
        }

        Gson gson = new Gson();
        PlayerGameResponse playerGameResponse = gson.fromJson(response.body(), PlayerGameResponse.class);

        return playerGameResponse.getArchives(); //Estrai correttamente la lista
    }

    /**
     * Scarica tutte le partite di un mese
     */
    public static List<Game> getMonthlyGames(String monthlyArchiveUrl) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(monthlyArchiveUrl))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Errore nella chiamata API: " + response.statusCode());
        }

        Gson gson = new Gson();
        return gson.fromJson(response.body(), MonthlyGameResponse.class).getGames();
    }
}