package org.example.api;

import org.example.model.Game;
import java.util.List;

/**
 * Rappresenta la risposta completa dell'API per un archivio mensile.
 * Contiene una lista di partite sotto la chiave "games".
 */
public class MonthlyGameResponse {
    private List<Game> games;

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }
}