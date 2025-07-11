package org.example.api;
import java.util.List;


/**
 * Rappresenta la risposta dell’API che contiene gli archivi mensili delle partite.
 * Ogni elemento della lista 'archives' è l'URL delle partite giocate in quel mese.
 */
public class PlayerGameResponse {
    private List<String> archives; // Lista di URL mensili delle partite

    public List<String> getArchives() {
        return archives;
    }

    public void setArchives(List<String> archives) {
        this.archives = archives;
    }
}