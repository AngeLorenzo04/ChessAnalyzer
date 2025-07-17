package org.example.UI;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.API.ChessCLI;

import java.io.IOException;

public class UsernameScreen extends VBox {
    public UsernameScreen(NavigationController controller, ChessCLI chessCLI) {
        setSpacing(10);
        setPadding(new Insets(15));

        Label label = new Label("Inserisci il nome utente Chess.com");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Nome utente");
        usernameField.setPrefWidth(300);

        Button nextButton = new Button("Avanti");
        nextButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            if (!username.isEmpty()) {
                // Comunica la selezione alla ChessCLI
                chessCLI.setUsername(username);
                try {
                    controller.showArchiveScreen(username);
                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        getChildren().addAll(label, usernameField, nextButton);
    }
}