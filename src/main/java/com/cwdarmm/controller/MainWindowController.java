package com.cwdarmm.controller;

/**
 * Controlador de la ventana principal que contiene las pestañas
 * "Markets" y "Results".
 */

import com.cwdarmm.config.SpringFXMLLoader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MainWindowController {

    @FXML private TabPane tabPane;
    @FXML private Tab tabMarkets;

    @FXML private Tab tabResults;
    @FXML private Tab tabMarketDb;
    @FXML private MenuBar menuBar;

    private final BdMarketController bdMarketController;
    private final ResultViewController resultViewController;
    private final SpringFXMLLoader springFXMLLoader;

    @FXML
    public void initialize() {
        // Seleccionamos la pestaña Markets por defecto al arrancar
        tabPane.getSelectionModel().select(tabMarkets);

        // Al cambiar a la pestaña BD Market recargamos su tabla
        tabMarketDb.setOnSelectionChanged(ev -> {
            if (tabMarketDb.isSelected()) {
                bdMarketController.refreshTable();
            }
        });

        // Al entrar a la pestaña Results recalculamos automáticamente
        tabResults.setOnSelectionChanged(ev -> {
            if (tabResults.isSelected()) {
                resultViewController.onClick();
            }
        });
    }

    // Métodos para cambiar de pestaña desde código si los necesitas:

    public void showResultsTab() {
        tabPane.getSelectionModel().select(tabResults);
    }

    @FXML
    private void switchToSpanish() throws IOException {
        reload(new Locale("es"));
    }

    @FXML
    private void switchToEnglish() throws IOException {
        reload(Locale.ENGLISH);
    }

    private void reload(Locale locale) throws IOException {
        // Guardamos la escena **antes** de recargar el FXML, porque al cargarse
        // de nuevo el controlador se reinicia y los nodos todavía no tienen escena.
        Scene scene = menuBar.getScene();
        springFXMLLoader.setLocale(locale);
        FXMLLoader loader = springFXMLLoader.load("/fxml/MainWindow.fxml");
        Parent root = loader.getRoot();
        scene.setRoot(root);
      //  scene.getWindow().setTitle(loader.getResources().getString("app.title"));
    }

    @FXML
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.showOpenDialog(menuBar.getScene().getWindow());
    }

    @FXML
    private void exit() {
        Platform.exit();
    }

    @FXML
    private void showAbout() throws IOException {
        FXMLLoader loader = springFXMLLoader.load("/fxml/AboutDialog.fxml");
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle(loader.getResources().getString("menu.about"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(menuBar.getScene().getWindow());
        stage.showAndWait();
    }
}
