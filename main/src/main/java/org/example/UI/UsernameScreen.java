package org.example.UI;// UsernameScreen.java
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class UsernameScreen extends VBox {
    public UsernameScreen(NavigationController controller) {
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
                controller.showArchiveScreen(username);
            }
        });

        getChildren().addAll(label, usernameField, nextButton);
    }
}