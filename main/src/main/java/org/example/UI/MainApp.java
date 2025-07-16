package org.example.UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final DataProvider dataProvider = new DataProvider();

    @Override
    public void start(Stage primaryStage) {

        ComboBox<String> listaCombo = new ComboBox<>();
        listaCombo.getItems().addAll(dataProvider.getNomiListe());
        listaCombo.setPromptText("Seleziona una lista");

        ComboBox<String> elementoCombo = new ComboBox<>();
        elementoCombo.setPromptText("Seleziona un elemento");
        elementoCombo.setDisable(true);

        Label sceltaLabel = new Label();

        listaCombo.setOnAction(e -> {
            String listaSelezionata = listaCombo.getValue();
            if (listaSelezionata != null) {
                elementoCombo.getItems().setAll(dataProvider.getElementiDiLista(listaSelezionata));
                elementoCombo.setDisable(false);
                elementoCombo.setValue(null);
                sceltaLabel.setText("");
            }
        });

        elementoCombo.setOnAction(e -> {
            String elementoSelezionato = elementoCombo.getValue();
            if (elementoSelezionato != null) {
                sceltaLabel.setText("Hai scelto: " + elementoSelezionato);
            }
        });

        VBox root = new VBox(15, listaCombo, elementoCombo, sceltaLabel);
        root.setStyle("-fx-padding: 20");

        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.setTitle("Selezione Lista ed Elemento");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
