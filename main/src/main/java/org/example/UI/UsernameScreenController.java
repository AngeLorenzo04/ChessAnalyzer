package org.example.UI;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.example.API.ChessCLI;
import java.io.IOException;

public class UsernameScreenController {
    @FXML private TextField usernameField;

    private NavigationController navigationController;
    private ChessCLI chessCLI;

    public void setNavigationController(NavigationController controller) {
        this.navigationController = controller;
    }

    public void setChessCLI(ChessCLI chessCLI) {
        this.chessCLI = chessCLI;
    }

    @FXML
    private void handleNextButton() {
        String username = usernameField.getText().trim();
        if (!username.isEmpty()) {
            chessCLI.setUsername(username);
            try {
                navigationController.showArchiveScreen(username);
            } catch (IOException | InterruptedException ex) {
                // Handle exception
                ex.printStackTrace();
            }
        }
    }
}