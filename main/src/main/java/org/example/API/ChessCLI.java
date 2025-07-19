package org.example.API;
import java.util.concurrent.CompletableFuture;

public class ChessCLI {
      // Riferimenti ai componenti UI
    private CompletableFuture<String> usernameFuture;
    private CompletableFuture<ChessArchive> archiveFuture;
    private CompletableFuture<ChessGame> gameFuture;

    public ChessCLI() {
        resetState();
    }

    private void resetState() {
        usernameFuture = new CompletableFuture<>();
        archiveFuture = new CompletableFuture<>();
        gameFuture = new CompletableFuture<>();
    }


    /**
     * Ottiene il nome utente dall'interfaccia grafica in modo asincrono
     */
    public CompletableFuture<String> promptUsername() {
        return usernameFuture;
    }

    /**
     * Ottiene l'archivio selezionato dall'interfaccia grafica in modo asincrono
     */
    public CompletableFuture<ChessArchive> selectArchive() {
        return archiveFuture;
    }

    /**
     * Ottiene la partita selezionata dall'interfaccia grafica in modo asincrono
     */
    public CompletableFuture<ChessGame> selectGame() {
        return gameFuture;
    }

}