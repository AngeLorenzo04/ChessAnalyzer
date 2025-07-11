package org.example.api;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChessComApiClient {

    private static final String BASE_URL = "https://api.chess.com/pub/player/";


    /**
     * Scarica la lista degli archivi mensili delle partite giocate dall'utente negli ultimi 12 mesi.
     *
     * @param username Nome utente su chess.com
     * @return Risposta dell'API contenente una lista di URL, uno per ogni mese
     * @throws Exception Se si verifica un errore durante la chiamata API
     */
    public static PlayerGameResponse getPlayerArchives(String username) throws Exception {
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
        return gson.fromJson(response.body(), PlayerGameResponse.class);
    }
}
