package com.cwdarmm.controller;

import com.cwdarmm.model.domain.CatAccount;
import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.domain.CatMarketData;
import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.service.ReferenceDataService;
import com.cwdarmm.service.MarketDataService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Controlador del formulario "Open Market".
 *
 * <p>Este diálogo permite registrar la apertura de un mercado y
 * captura la misma información que el formulario VBA
 * <code>MARKET</code> y sus validaciones. Almacena la selección de
 * cuenta, mercado, fuente de datos y porcentajes de riesgo. Las
 * reglas de validación siguen la lógica de <code>risk_market.frm</code>
 * del Excel original.</p>
 */

import java.math.BigDecimal;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class OpenMarketSessionController {
    private final ReferenceDataService referenceDataService;
    private final MarketDataService marketDataService;
    private Stage dialogStage;
    private Runnable onSaveCallback;
    private MarketDTO lastSaved;

    private MarketDTO existingDto;

    @FXML
    private ComboBox<CatAccount> cbAccount;
    @FXML
    private ComboBox<CatMarket> cbMarket;
    @FXML
    private ComboBox<CatMarketData> cbMarketData;
    @FXML
    private TextField tfAccountSize;
    @FXML
    private TextField tfRiskA;
    @FXML
    private TextField tfRiskB;
    @FXML private ResourceBundle resources;

    @FXML
    public void initialize() {
        // 1) Poblar Account
        cbAccount.setItems(FXCollections.observableArrayList(
                referenceDataService.listAccounts()));
        cbAccount.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(CatAccount item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDescription());
            }
        });
        cbAccount.setConverter(new StringConverter<>() {
            @Override
            public String toString(CatAccount a) {
                return (a == null ? "" : a.getDescription());
            }

            @Override
            public CatAccount fromString(String s) {
                return null;
            }
        });

        // 2) Poblar Market (estático o según negocio)
        cbMarket.setItems(FXCollections.observableArrayList(
                referenceDataService.listMarkets()));
        cbMarket.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(CatMarket m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : m.getDescription());
            }
        });
        cbMarket.setConverter(new StringConverter<>() {
            @Override
            public String toString(CatMarket m) {
                return (m == null ? "" : m.getDescription());
            }

            @Override
            public CatMarket fromString(String s) {
                return null;
            }
        });

        // 3) MarketData deshabilitado hasta seleccionar Account
        cbMarketData.setDisable(true);
        cbAccount.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                // Cargar Market Data por Account ID
                cbMarketData.setItems(FXCollections.observableArrayList(
                        referenceDataService.listFeedsByAccount(sel.getId())));
                cbMarketData.setCellFactory(list -> new ListCell<>() {
                    @Override
                    protected void updateItem(CatMarketData f, boolean empty) {
                        super.updateItem(f, empty);
                        setText(empty || f == null ? null : f.getDescription());
                    }
                });
                cbMarketData.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(CatMarketData f) {
                        return (f == null ? "" : f.getDescription());
                    }

                    @Override
                    public CatMarketData fromString(String s) {
                        return null;
                    }
                });
                cbMarketData.setDisable(false);
            } else {
                cbMarketData.getItems().clear();
                cbMarketData.setDisable(true);
            }
        });
    }

    @FXML
    private void onOpenMarket() {
        if (!validateInputs()) return;

        MarketDTO dto = MarketDTO.builder()
                .account(cbAccount.getValue())
                .market(cbMarket.getValue())
                .marketData(cbMarketData.getValue())
                .accountSize(new BigDecimal(tfAccountSize.getText()))
                .riskA(tfRiskA.getText().isBlank() ? 0d : Double.parseDouble(tfRiskA.getText()))
                .riskB(tfRiskB.getText().isBlank() ? 0d : Double.parseDouble(tfRiskB.getText()))
                .build();
        if (existingDto != null) {
            dto.setId(existingDto.getId());
        }
        lastSaved = marketDataService.save(dto);
        if (onSaveCallback != null) onSaveCallback.run();
        dialogStage.close();
    }

    @FXML
    private void onClose() {
        dialogStage.close();
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setOnSave(Runnable callback) {
        this.onSaveCallback = callback;
    }

    public MarketDTO getLastSavedDTO() {
        return lastSaved;
    }

    private boolean validateInputs() {
        if (cbAccount.getValue() == null || cbMarket.getValue() == null || cbMarketData.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, resources.getString("alert.openmarket.missing"));
            return false;
        }
        try {
            double size = Double.parseDouble(tfAccountSize.getText());
            if (size <= 0) throw new NumberFormatException();
            if (!tfRiskA.getText().isBlank()) {
                double a = Double.parseDouble(tfRiskA.getText());
                if (a < 0) throw new NumberFormatException();
            }
            if (!tfRiskB.getText().isBlank()) {
                double b = Double.parseDouble(tfRiskB.getText());
                if (b < 0) throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, resources.getString("alert.openmarket.number"));
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.initOwner(dialogStage);
        alert.setTitle(resources.getString("alert.validation"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Inicializa el formulario:
     *  - si dto == null, limpia todos los campos para crear uno nuevo
     *  - si dto != null, precarga para edición
     */
    public void initForm(MarketDTO dto) {
        this.existingDto = dto;
        if (dto == null) {
            // Nuevo registro: limpia selecciones y campos
            cbAccount.getSelectionModel().clearSelection();
            cbMarket .getSelectionModel().clearSelection();
            cbMarketData.getItems().clear();
            cbMarketData.setDisable(true);
            tfAccountSize.clear();
            tfRiskA.clear();
            tfRiskB.clear();
        } else {
            // Edición: tu código actual de setExistingDTO
            cbAccount.getSelectionModel().select(dto.getAccount());
            cbMarket .getSelectionModel().select(dto.getMarket());
            cbMarketData.setDisable(false);
            cbMarketData.setItems(FXCollections.observableArrayList(
                    referenceDataService.listFeedsByAccount(dto.getAccount().getId())
            ));
            cbMarketData.getSelectionModel().select(dto.getMarketData());
            tfAccountSize.setText(dto.getAccountSize().toPlainString());
            tfRiskA.setText(String.valueOf(dto.getRiskA()));
            tfRiskB.setText(String.valueOf(dto.getRiskB()));
        }
    }

}
