package com.cwdarmm.controller;

import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.model.domain.AccountDefinition;
import com.cwdarmm.model.domain.FeedDefinition;
import com.cwdarmm.model.domain.MarketMaster;
import com.cwdarmm.service.CatalogService;
import com.cwdarmm.service.MarketService;
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

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class OpenMarketController {
    private final CatalogService catalogService;
    private final MarketService marketService;
    private Stage dialogStage;
    private Runnable onSaveCallback;
    private MarketDTO lastSaved;

    private MarketDTO existingDto;

    @FXML
    private ComboBox<AccountDefinition> cbAccount;
    @FXML
    private ComboBox<MarketMaster> cbMarket;
    @FXML
    private ComboBox<FeedDefinition> cbMarketData;
    @FXML
    private TextField tfAccountSize;
    @FXML
    private TextField tfRiskA;
    @FXML
    private TextField tfRiskB;

    @FXML
    public void initialize() {
        // 1) Poblar Account
        cbAccount.setItems(FXCollections.observableArrayList(
                catalogService.listAccounts()));
        cbAccount.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(AccountDefinition item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        cbAccount.setConverter(new StringConverter<>() {
            @Override
            public String toString(AccountDefinition a) {
                return (a == null ? "" : a.getName());
            }

            @Override
            public AccountDefinition fromString(String s) {
                return null;
            }
        });

        // 2) Poblar Market (estático o según negocio)
        cbMarket.setItems(FXCollections.observableArrayList(
                catalogService.listMarkets()));
        cbMarket.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(MarketMaster m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : m.getName());
            }
        });
        cbMarket.setConverter(new StringConverter<>() {
            @Override
            public String toString(MarketMaster m) {
                return (m == null ? "" : m.getName());
            }

            @Override
            public MarketMaster fromString(String s) {
                return null;
            }
        });

        // 3) MarketData deshabilitado hasta seleccionar Account
        cbMarketData.setDisable(true);
        cbAccount.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                // Cargar Market Data por Account ID
                cbMarketData.setItems(FXCollections.observableArrayList(
                        catalogService.listFeedsByAccount(sel.getId())));
                cbMarketData.setCellFactory(list -> new ListCell<>() {
                    @Override
                    protected void updateItem(FeedDefinition f, boolean empty) {
                        super.updateItem(f, empty);
                        setText(empty || f == null ? null : f.getName());
                    }
                });
                cbMarketData.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(FeedDefinition f) {
                        return (f == null ? "" : f.getName());
                    }

                    @Override
                    public FeedDefinition fromString(String s) {
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
                .riskA(Double.parseDouble(tfRiskA.getText()))
                .riskB(Double.parseDouble(tfRiskB.getText()))
                .build();
        if (existingDto != null) {
            dto.setId(existingDto.getId());
        }
        lastSaved = marketService.save(dto);
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
            showAlert(Alert.AlertType.WARNING, "Debe seleccionar Account, Market y Market Data.");
            return false;
        }
        try {
            double size = Double.parseDouble(tfAccountSize.getText());
            double a = Double.parseDouble(tfRiskA.getText());
            double b = Double.parseDouble(tfRiskB.getText());
            if (size <= 0 || a <= 0 || b <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "Account Size, Risk A y Risk B deben ser números mayores a cero.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.initOwner(dialogStage);
        alert.setTitle("Validación");
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
                    catalogService.listFeedsByAccount(dto.getAccount().getId())
            ));
            cbMarketData.getSelectionModel().select(dto.getMarketData());
            tfAccountSize.setText(dto.getAccountSize().toPlainString());
            tfRiskA.setText(String.valueOf(dto.getRiskA()));
            tfRiskB.setText(String.valueOf(dto.getRiskB()));
        }
    }

}
