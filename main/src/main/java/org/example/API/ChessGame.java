package org.example.API;

public class ChessGame {
    private final String pgn;
    private final long timestamp;

    public ChessGame(String pgn, long timestamp) {
        this.pgn = pgn;
        this.timestamp = timestamp;
    }

    public String getPgn() {
        return pgn;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
