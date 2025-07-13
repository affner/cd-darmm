package com.cwdarmm.controller;

import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.service.MarketService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Controlador para el formulario "Open Market".
 * Spring @Component para inyección de dependencias.
 */
@Component
@RequiredArgsConstructor
public class OpenMarketController {
    private final MarketService marketService;

    @FXML private ComboBox<String> cbAccount;
    @FXML private ComboBox<String> cbMarket;
    @FXML private ComboBox<String> cbMarketData;
    @FXML private TextField tfAccountSize;
    @FXML private TextField tfRiskA;
    @FXML private TextField tfRiskB;
    @FXML private Button btnOpen;
    @FXML private Button btnClose;

    private Stage dialogStage;
    private Runnable onSaveCallback;
    private MarketDTO lastSaved;

    /**
     * Inicializa los ComboBoxes con datos del servicio.
     */
    @FXML
    public void initialize() {
        cbAccount.setItems(FXCollections.observableArrayList(marketService.listAccounts()));
        cbMarket.setItems(FXCollections.observableArrayList(marketService.listMarkets()));
        cbMarketData.setItems(FXCollections.observableArrayList(marketService.listMarketData()));
    }

    /**
     * Setter para el Stage del diálogo.
     */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /**
     * Callback invocado después de guardar para refrescar las tablas.
     */
    public void setOnSave(Runnable callback) {
        this.onSaveCallback = callback;
    }

    /**
     * Acción del botón "Open Market".
     * Valida, crea DTO y persiste vía MarketService.
     */
    @FXML
    private void onOpenMarket() {
        if (!validateInputs()) return;

        MarketDTO dto = MarketDTO.builder()
                .account(cbAccount.getValue())
                .market(cbMarket.getValue())
                .marketData(cbMarketData.getValue())
                .accountSize(Double.parseDouble(tfAccountSize.getText()))
                .riskA(Double.parseDouble(tfRiskA.getText()))
                .riskB(Double.parseDouble(tfRiskB.getText()))
                .build();

        lastSaved = marketService.save(dto);
        if (onSaveCallback != null) onSaveCallback.run();
        dialogStage.close();
    }

    /**
     * Devuelve el último MarketDTO guardado.
     */
    public MarketDTO getLastSavedDTO() {
        return lastSaved;
    }

    /**
     * Cierra el diálogo sin guardar.
     */
    @FXML
    private void onClose() {
        dialogStage.close();
    }

    /**
     * Validaciones básicas: selección obligatoria y valores numéricos > 0.
     */
    private boolean validateInputs() {
        if (cbAccount.getValue() == null || cbMarket.getValue() == null || cbMarketData.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Debe seleccionar Account, Market y Market Data.");
            return false;
        }
        try {
            double accSize = Double.parseDouble(tfAccountSize.getText());
            double a = Double.parseDouble(tfRiskA.getText());
            double b = Double.parseDouble(tfRiskB.getText());
            if (accSize <= 0 || a <= 0 || b <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "Account Size, Risk A y Risk B deben ser números mayores a cero.");
            return false;
        }
        return true;
    }

    /**
     * Muestra un Alert genérico.
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.initOwner(dialogStage);
        alert.setTitle("Validación");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
