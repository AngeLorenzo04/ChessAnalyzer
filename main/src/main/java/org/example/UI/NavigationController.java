package org.example.UI;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.API.ChessArchive;
import org.example.API.ChessCLI;
import org.example.API.ChessGame;

import java.io.IOException;
import java.util.List;

public class NavigationController {
    private final Stage primaryStage;
    private final ChessCLI chessCLI;
    private String username;
    private ChessArchive selectedArchive;
    private ChessGame selectedGame;

    public NavigationController(Stage primaryStage, ChessCLI chessCLI) {
        this.primaryStage = primaryStage;
        this.chessCLI = chessCLI;
    }

    public void showUsernameScreen() {
        UsernameScreen screen = new UsernameScreen(this, chessCLI);
        primaryStage.setScene(new Scene(screen, 600, 400));
    }

    public void showArchiveScreen(String username) throws IOException, InterruptedException {
        this.username = username;
        List<ChessArchive> archives = DataService.getArchivesForUser(username);
        ArchiveScreen screen = new ArchiveScreen(this, archives);
        primaryStage.setScene(new Scene(screen, 600, 400));
    }

    public void showGameScreen(ChessArchive archive) throws IOException, InterruptedException {
        this.selectedArchive = archive;
        List<ChessGame> games = DataService.getGamesForArchive(archive);
        GameScreen screen = new GameScreen(this, games);
        primaryStage.setScene(new Scene(screen, 600, 400));
    }

    public void showPGNScreen(ChessGame game) {
        this.selectedGame = game;
        PGNScreen screen = new PGNScreen(this, game);
        primaryStage.setScene(new Scene(screen, 600, 500));

        // Mostra il PGN nell'interfaccia
        chessCLI.displayPGN();
    }

    public void printGameInfo() {
        DataService.printGameInfo(username, selectedArchive, selectedGame);
    }

    public String getUsername() {
        return username;
    }
}