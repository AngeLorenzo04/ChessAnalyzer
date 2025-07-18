package org.example.UI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.example.API.ChessArchive;
import org.example.UI.stringConverter.ArchiveStringConverter;

import java.util.List;

public class ArchiveScreenController {

    public Button nextButton;
    public Button backButton;
    @FXML private Label usernameLabel;
    @FXML private ComboBox<ChessArchive> archiveCombo;

    private NavigationController navigationController;
    private List<ChessArchive> archives;

    public void setNavigationController(NavigationController navigationController) {
        this.navigationController = navigationController;
        updateUsernameLabel();
    }

    public void setArchives(List<ChessArchive> archives) {
        this.archives = archives;
        archiveCombo.getItems().setAll(archives);
    }

    private void updateUsernameLabel() {
        if(navigationController != null) {
            usernameLabel.setText("Seleziona un archivio per " + navigationController.getUsername());
        }
    }

    @FXML
    private void initialize() {
        archiveCombo.setConverter(new ArchiveStringConverter());
    }

    @FXML
    private void handleNextButton() {
        ChessArchive selected = archiveCombo.getValue();
        if (selected != null && navigationController != null) {
            try {
                navigationController.showGameScreen(selected, navigationController.getUsername());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @FXML
    private void handleBackButton() {
        if(navigationController != null) {
            navigationController.showUsernameScreen();
        }
    }
}