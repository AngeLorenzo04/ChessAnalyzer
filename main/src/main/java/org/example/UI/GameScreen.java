package org.example.UI;// GameScreen.java
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.example.API.ChessGame;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GameScreen extends VBox {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    public GameScreen(NavigationController controller, List<ChessGame> games) {
        setSpacing(10);
        setPadding(new Insets(15));

        Label label = new Label("Seleziona una partita");
        ComboBox<ChessGame> gameCombo = new ComboBox<>();
        gameCombo.setItems(FXCollections.observableArrayList(games));
        gameCombo.setConverter(new GameStringConverter());
        gameCombo.setPrefWidth(300);

        Button nextButton = new Button("Avanti");
        nextButton.setOnAction(e -> {
            ChessGame selected = gameCombo.getValue();
            if (selected != null) {
                controller.showPGNScreen(selected);
            }
        });

        Button backToNameButton = new Button("Torna alla scelta del nome");
        backToNameButton.setOnAction(e -> controller.showUsernameScreen());

        Button backToArhchivie = new Button("Torna alla scelta del archivio");
        backToNameButton.setOnAction(e -> controller.showUsernameScreen());

        getChildren().addAll(label, gameCombo, nextButton,backToNameButton, backToArhchivie);
    }

    private static class GameStringConverter extends StringConverter<ChessGame> {
        @Override
        public String toString(ChessGame game) {
            if (game == null) return "";
            String date = DATE_FORMATTER.format(Instant.ofEpochSecond(game.getTimestamp()));
            return "Partita del " + date;
        }

        @Override
        public ChessGame fromString(String string) {
            return null;
        }
    }
}
