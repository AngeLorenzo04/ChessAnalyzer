package org.example.api;


import java.util.List;

public class PlayerGameResponse {
    private List<String> archives; // Lista di URL mensili delle partite

    public List<String> getArchives() {
        return archives;
    }

    public void setArchives(List<String> archives) {
        this.archives = archives;
    }
}