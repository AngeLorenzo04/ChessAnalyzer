package org.example.UI;// PGNScreen.java
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.example.API.ChessGame;

public class PGNScreen extends VBox {
    public PGNScreen(NavigationController controller, ChessGame game) {
        setSpacing(10);
        setPadding(new Insets(15));

        Label label = new Label("PGN della partita selezionata");
        TextArea pgnArea = new TextArea(game.getPgn());
        pgnArea.setWrapText(true);
        pgnArea.setEditable(false);
        pgnArea.setPrefHeight(300);

        Button printButton = new Button("Stampa nome partita");
        printButton.setOnAction(e -> controller.printGameInfo());

        getChildren().addAll(label, pgnArea, printButton);
    }
}