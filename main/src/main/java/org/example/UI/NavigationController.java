package org.example.UI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.API.ChessArchive;
import org.example.API.ChessCLI;
import org.example.API.ChessGame;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class NavigationController {
    private  Stage primaryStage = new Stage();
    private  ChessCLI chessCLI = new ChessCLI();
    private String username;
    private ChessArchive selectedArchive;
    private ChessGame selectedGame;


    public NavigationController(Stage primaryStage, ChessCLI chessCLI) {
        this.primaryStage = primaryStage;
        this.chessCLI = chessCLI;
    }

    public void showUsernameScreen() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UsernameScene.fxml"));
            Parent root = loader.load();

            UsernameScreenController controller = loader.getController();
            controller.setNavigationController(this);
            controller.setChessCLI(chessCLI);

            // Set or update the scene
            if (primaryStage.getScene() != null) {
                primaryStage.setScene(new Scene(root, 600, 400));
            } else {
                primaryStage.getScene().setRoot(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showArchiveScreen(String username) throws IOException, InterruptedException {
        this.username = username;
        List<ChessArchive> archives = DataService.getArchivesForUser(username);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UsernameScene.fxml"));

            // 2. Caricare la schermata
            VBox archiveScreen = loader.load();

            // 3. Ottenere il controller CORRETTO
            ArchiveScreenController controller = loader.getController();

            // 4. Configurare il controller
            controller.setNavigationController(this);
            controller.setArchives(archives);

            // 5. Sostituire la scena corrente
            primaryStage.setScene(archiveScreen.getScene());
        } catch (IOException e) {
            throw new RuntimeException("Error loading ArchiveScreen FXML", e);
        }
    }

    public void showGameScreen(ChessArchive archive,String username) throws IOException, InterruptedException {
        this.selectedArchive = archive;
        this.username = username;
        List<ChessGame> games = DataService.getGamesForArchive(archive);
        GameScreen screen = new GameScreen(this, games,username);
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
