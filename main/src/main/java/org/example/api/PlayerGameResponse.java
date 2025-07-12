package org.example.api;

import java.util.List;

/**
 * Rappresenta la risposta dell’API che contiene gli archivi mensili delle partite
 */
public class PlayerGameResponse {
    private List<String> archives;

    public List<String> getArchives() {
        return archives;
    }

    public void setArchives(List<String> archives) {
        this.archives = archives;
    }
}