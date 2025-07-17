package org.example.UI;// NavigationController.java
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.API.ChessArchive;
import org.example.API.ChessGame;

import java.util.List;

public class NavigationController {
    private final Stage primaryStage;
    private String username;
    private ChessArchive selectedArchive;
    private ChessGame selectedGame;

    public NavigationController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showUsernameScreen() {
        UsernameScreen screen = new UsernameScreen(this);
        primaryStage.setScene(new Scene(screen, 600, 400));
    }

    public void showArchiveScreen(String username) {
        this.username = username;
        List<ChessArchive> archives = DataService.getArchivesForUser(username);
        ArchiveScreen screen = new ArchiveScreen(this, archives);
        primaryStage.setScene(new Scene(screen, 600, 400));
    }

    public void showGameScreen(ChessArchive archive) {
        this.selectedArchive = archive;
        List<ChessGame> games = DataService.getGamesForArchive(archive);
        GameScreen screen = new GameScreen(this, games);
        primaryStage.setScene(new Scene(screen, 600, 400));
    }

    public void showPGNScreen(ChessGame game) {
        this.selectedGame = game;
        PGNScreen screen = new PGNScreen(this, game);
        primaryStage.setScene(new Scene(screen, 600, 500));
    }

    public void printGameInfo() {
        DataService.printGameInfo(username, selectedArchive, selectedGame);
    }

    public String getUsername() {
        return username;
    }
}