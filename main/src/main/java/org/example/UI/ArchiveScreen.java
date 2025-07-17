package org.example.UI;// ArchiveScreen.java
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.API.ChessArchive;

import java.util.List;

public class ArchiveScreen extends VBox {
    public ArchiveScreen(NavigationController controller, List<ChessArchive> archives) {
        setSpacing(10);
        setPadding(new Insets(15));

        Label label = new Label("Seleziona un archivio per " + controller.getUsername());
        ComboBox<ChessArchive> archiveCombo = new ComboBox<>();
        archiveCombo.setItems(FXCollections.observableArrayList(archives));
        archiveCombo.setConverter(new ArchiveStringConverter());
        archiveCombo.setPrefWidth(300);

        Button nextButton = new Button("Avanti");
        nextButton.setOnAction(e -> {
            ChessArchive selected = archiveCombo.getValue();
            if (selected != null) {
                controller.showGameScreen(selected);
            }
        });

        Button backButton = new Button("Indietro");
        backButton.setOnAction(e -> {
            controller.showUsernameScreen();
        });

        getChildren().addAll(label, archiveCombo, nextButton,backButton);
    }
}