package org.example.API;

public class ChessArchive {
    private final String url;
    private final String year;
    private final String month;

    public ChessArchive(String url) {
        this.url = url;

        String[] parts = url.split("/");
        this.year = parts[parts.length - 2];
        this.month = parts[parts.length - 1];
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return year + "/" + month;
    }
}
