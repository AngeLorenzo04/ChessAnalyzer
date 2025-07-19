package org.example.UI.board;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

// Classe dedicata solo agli stili CSS
public class ChessStyles {

    // Stili per la scacchiera
    public static void applyMainLayoutStyle(HBox layout) {
        layout.getStyleClass().add("main-layout");
    }

    public static void applyBoardBoxStyle(VBox boardBox) {
        boardBox.getStyleClass().add("board-box");
    }

    public static void applyTitleStyle(Label title) {
        title.getStyleClass().add("title-label");
    }



    // Stili per il pannello commenti
    public static void applyCommentPanelStyle(VBox panel) {
        panel.getStyleClass().add("comment-panel");
    }

    public static void applyScrollPaneStyle(ScrollPane scroll) {
        scroll.getStyleClass().add("scroll-pane");
    }

    public static void applyCommentTitleStyle(Label title) {
        title.getStyleClass().add("comment-title");
    }

    // Stili per i pulsanti
    public static void applyButtonStyle(Button button, String type) {
        button.getStyleClass().addAll("chess-button", type + "-button");
    }
}