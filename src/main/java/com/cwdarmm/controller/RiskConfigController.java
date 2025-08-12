package com.cwdarmm.controller;

/**
 * Diálogo para configurar los parámetros de riesgo de un trade
 * y lanzar los cálculos correspondientes.
 */

import com.cwdarmm.model.domain.CatAccount;
import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.domain.CatMarketData;
import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.model.dto.OptimalContractRow;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.service.ReferenceDataService;
import com.cwdarmm.service.ExportService;
import com.cwdarmm.service.RiskAnalysisService;
import com.cwdarmm.service.RiskContext;
import com.cwdarmm.controller.BdMarketController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import com.cwdarmm.config.SpringFXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class RiskConfigController {
    private final RiskAnalysisService riskAnalysisService;
    private final ReferenceDataService referenceDataService;
    private final ExportService exportService;
    private final SpringFXMLLoader springFXMLLoader;
    private final BdMarketController bdMarketController;
    private final RiskContext riskContext;
    private Stage dialogStage;
    private Consumer<RiskInputDTO> onCalculated;
    private MarketDTO marketContext;
    private java.math.BigDecimal pctHouse;
    private java.math.BigDecimal pctLunch;

    @FXML
    private ComboBox<CatAccount> cbRiskAccount;
    @FXML
    private ComboBox<CatMarket> cbRiskMarket;
    @FXML
    private ComboBox<CatMarketData> cbRiskMarketData;
    @FXML
    private TextField tfRiskAccountSize;
    @FXML
    private TextField tfRiskReward;
    @FXML
    private TextField tfTicksSl1;
    @FXML
    private TextField tfTicksSl2;
    @FXML
    private TextField tfStopLossSize;
    @FXML
    private CheckBox chkHouse;
    @FXML
    private CheckBox chkLunch;
    @FXML
    private CheckBox chkWin;
    @FXML
    private CheckBox chkLoss;
    @FXML
    private ImageView imgCoins;

    @FXML
    public void initialize() {
        // 1) Cargamos cuentas
        List<CatAccount> accounts = referenceDataService.listAccounts();
        cbRiskAccount.setItems(FXCollections.observableArrayList(accounts));
        setupCombo(cbRiskAccount, CatAccount::getDescription);
        // 2) Markets (static list)
        List<CatMarket> markets = referenceDataService.listMarkets();
        cbRiskMarket.setItems(FXCollections.observableArrayList(markets));

        // Disable MarketData until account selectedﬁ
        cbRiskMarketData.setDisable(true);
        setupCombo(cbRiskMarket, CatMarket::getDescription);
        // 3) Al cambiar Account → cargamos Markets asociados
        cbRiskAccount.getSelectionModel().selectedItemProperty().addListener((obs, oldAcc, newAcc) -> {
            if (newAcc != null) {
                // find accountId
                Long accId = newAcc.getId();
                // load feeds for that account
                List<CatMarketData> feeds = referenceDataService.listFeedsByAccount(accId);
                cbRiskMarketData.setItems(FXCollections.observableArrayList(feeds));
                cbRiskMarketData.setDisable(false);
            } else {
                cbRiskMarketData.getItems().clear();
                cbRiskMarketData.setDisable(true);
            }
        });
        setupCombo(cbRiskMarketData, CatMarketData::getDescription);

        // Carga imagen
        imgCoins.setImage(new Image(getClass().getResourceAsStream("/img/coins.png")));
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setMarketContext(MarketDTO context) {
        this.marketContext = context;
        cbRiskAccount.setValue(context.getAccount());
        cbRiskMarket.setValue(context.getMarket());
        cbRiskMarketData.setValue(context.getMarketData());
        this.pctHouse = java.math.BigDecimal.valueOf(context.getRiskA());
        this.pctLunch = java.math.BigDecimal.valueOf(context.getRiskB());
    }


    /** Para compatibilidad con viejos callers que usaban Runnable */
    public void setOnCalculated(Runnable callback) {
        this.onCalculated = dto -> callback.run();
    }

    /** La nueva sobrecarga que infiere bien el tipo de req */
    public void setOnCalculated(Consumer<RiskInputDTO> callback) {
        this.onCalculated = callback;
    }

    @FXML
    private void onCalculate() throws IOException {
        if (!validate()) return;

        // 1) ¿Es este el primer trade?
        Alert firstAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Es este el primer trade?", ButtonType.YES, ButtonType.NO);
        firstAlert.initOwner(dialogStage);
        boolean first = firstAlert
                .showAndWait()
                .filter(ButtonType.YES::equals)
                .isPresent();

        // 2) ¿Generar XML?
        Alert xmlAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas generar el XML de estrategia?", ButtonType.YES, ButtonType.NO);
        xmlAlert.initOwner(dialogStage);
        boolean doXml = xmlAlert
                .showAndWait()
                .filter(ButtonType.YES::equals)
                .isPresent();

        // 3) ¿Generar AHK?
        Alert ahkAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas generar el script AutoHotkey?", ButtonType.YES, ButtonType.NO);
        ahkAlert.initOwner(dialogStage);
        boolean doAhk = ahkAlert
                .showAndWait()
                .filter(ButtonType.YES::equals)
                .isPresent();

        // 4) Construimos el DTO con el flag
        //    Excel sobrescribe el tamaño de la cuenta con el valor
        //    configurado inicialmente cuando es el primer trade
        //    (risk_market.frm), por lo que replicamos ese comportamiento
        //    antes de continuar con los cálculos.
        RiskInputDTO req = buildRequest()
                .toBuilder()
                .firstTrade(first)
                .build();

        if (first) {
            // Si es el primer trade, usamos el tamaño inicial de la
            // cuenta asociado a la selección, ignorando lo que haya
            // introducido el usuario en el formulario.
            req = req.toBuilder()
                    .accountSize(BigDecimal.valueOf(req.getAccount().getInitialSize()))
                    .build();
        }

        // 5) Llamamos al servicio que calcula el nuevo riesgo y registra el trade
        List<RiskResultDTO> rows = riskAnalysisService.calculate(req);

        // 5.a) Actualizamos los porcentajes de riesgo con los valores devueltos
        if (!rows.isEmpty()) {
            RiskResultDTO last = rows.get(rows.size() - 1);
            if (last.getRiskKellyA() != null) {
                this.pctHouse = java.math.BigDecimal.valueOf(last.getRiskKellyA());
            }
            if (last.getRiskKellyB() != null) {
                this.pctLunch = java.math.BigDecimal.valueOf(last.getRiskKellyB());
            }
            // reconstruimos el request con los porcentajes ajustados
            req = req.toBuilder()
                    .riskPctA(pctHouse)
                    .riskPctB(pctLunch)
                    .build();
        }

        // Guardamos la configuración para reutilizarla en otras vistas (Results)
        riskContext.set(req);

        // 6) Export si el usuario lo pidió
        if (doXml) {
            Path xml = Path.of(System.getProperty("user.home"),
                    marketContext.getMarket().getDescription() + ".xml");
            exportService.exportStrategyToXml(marketContext, rows, xml);
            new Alert(Alert.AlertType.INFORMATION,
                    "XML generado en:\n" + xml).showAndWait();
        }
        if (doAhk) {
            Path ahk = Path.of(System.getProperty("user.home"),
                    marketContext.getMarket().getDescription() + ".ahk");
            exportService.exportStrategyToAhk(marketContext, rows, ahk);
            new Alert(Alert.AlertType.INFORMATION,
                    "AHK generado en:\n" + ahk).showAndWait();
        }

        // 7) Refrescar tabla (tu callback monta estas filas en la TableView)
        if (onCalculated != null) {
            onCalculated.accept(req);   // ahora le paso el req con riesgo actualizado
        }

        // 8) Mostrar ventana de Optimal Contracts
        List<OptimalContractRow> optimalRows = riskAnalysisService.generateOptimalContracts(req);
        showOptimalContracts(optimalRows, req.getAccount().getDescription(), req.getMarket());
        bdMarketController.refreshTable();
        dialogStage.close();
    }

    private void showOptimalContracts(List<OptimalContractRow> rows, String criteriaAccount, CatMarket market) throws IOException {
        FXMLLoader loader = springFXMLLoader.load("/fxml/OptimalContractsView.fxml");
        OptimalContractsController ctrl = loader.getController();
        ctrl.setCriteriaAccount(criteriaAccount);
        ctrl.setMarket(market);
        ctrl.setItems(rows);

        Stage popup = new Stage();
        if (dialogStage != null && dialogStage.getOwner() != null) {
            popup.initOwner(dialogStage.getOwner());
        }
        popup.initModality(Modality.NONE);
        popup.setTitle(criteriaAccount + " – Optimal Contracts");
        popup.setScene(new Scene(loader.getRoot()));
        popup.show();
    }

    @FXML
    private void onClose() {
        dialogStage.close();
    }

    // ————— Helpers —————

    private <T> void setupCombo(ComboBox<T> combo, java.util.function.Function<T, String> toString) {
        combo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : toString.apply(item));
            }
        });
        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : toString.apply(item));
            }
        });
        combo.setConverter(new StringConverter<>() {
            @Override
            public String toString(T obj) {
                return obj == null ? "" : toString.apply(obj);
            }

            @Override
            public T fromString(String s) {
                return null;
            }
        });
    }

    private boolean validate() {
        if (cbRiskAccount.getValue() == null || cbRiskMarket.getValue() == null || cbRiskMarketData.getValue() == null) {
            alert("Selecciona Account, Market y Market Data.");
            return false;
        }
        if (!chkHouse.isSelected() && !chkLunch.isSelected()) {
            alert("Selecciona al menos HOUSE o LUNCH.");
            return false;
        }
        if (!chkWin.isSelected() && !chkLoss.isSelected()) {
            alert("Selecciona al menos WIN o LOSS.");
            return false;
        }
        try {
            if (Double.parseDouble(tfRiskAccountSize.getText()) <= 0 ||
                    Double.parseDouble(tfRiskReward.getText()) <= 0 ||
                    Integer.parseInt(tfTicksSl1.getText()) <= 0 ||
                    Integer.parseInt(tfTicksSl2.getText()) <= 0) throw new Exception();
        } catch (Exception e) {
            alert("Revisa los valores numéricos.");
            return false;
        }
        return true;
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.initOwner(dialogStage);
        a.setTitle("Validación");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /**
     * Devuelve un RiskInputDTO construido con los valores actuales del formulario
     */
    public RiskInputDTO buildRequest() {
        return RiskInputDTO.builder()
                .account(cbRiskAccount.getValue())
                .market(cbRiskMarket.getValue())
                .marketData(cbRiskMarketData.getValue())
                .accountSize(new BigDecimal(tfRiskAccountSize.getText()))
                .riskReward(Double.parseDouble(tfRiskReward.getText()))
                .ticksSl1(Integer.parseInt(tfTicksSl1.getText()))
                .ticksSl2(Integer.parseInt(tfTicksSl2.getText()))
                .stopLossSize(tfStopLossSize.getText().isEmpty() ? 0 : Integer.parseInt(tfStopLossSize.getText()))
                .house(chkHouse.isSelected())
                .lunch(chkLunch.isSelected())
                .win(chkWin.isSelected())
                .loss(chkLoss.isSelected())
                .riskPctA(pctHouse)
                .riskPctB(pctLunch)
                .build();
    }
}
