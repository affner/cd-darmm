package com.cwdarmm.controller;

import com.cwdarmm.model.domain.AccountDefinition;
import com.cwdarmm.model.domain.FeedDefinition;
import com.cwdarmm.model.domain.MarketMaster;
import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.service.CatalogService;
import com.cwdarmm.service.ExportService;
import com.cwdarmm.service.RiskService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RiskFormController {
    private final RiskService riskService;
    private final CatalogService catalogService;
    private final ExportService exportService;
    private Stage dialogStage;
    private Runnable onCalculated;
    private MarketDTO marketContext;

    @FXML private ComboBox<AccountDefinition> cbRiskAccount;
    @FXML private ComboBox<MarketMaster>    cbRiskMarket;
    @FXML private ComboBox<FeedDefinition>  cbRiskMarketData;
    @FXML private TextField tfRiskAccountSize;
    @FXML private TextField tfRiskReward;
    @FXML private TextField tfTicksSl1;
    @FXML private TextField tfTicksSl2;
    @FXML private TextField tfStopLossSize;
    @FXML private CheckBox chkHouse;
    @FXML private CheckBox chkLunch;
    @FXML private CheckBox chkWin;
    @FXML private CheckBox chkLoss;
    @FXML private ImageView imgCoins;

    @FXML
    public void initialize() {
        // 1) Cargamos cuentas
        List<AccountDefinition> accounts = catalogService.listAccounts();
        cbRiskAccount.setItems(FXCollections.observableArrayList(accounts));
        setupCombo(cbRiskAccount, AccountDefinition::getName);
        // 2) Markets (static list)
        List<MarketMaster> markets = catalogService.listMarkets();
        cbRiskMarket.setItems(FXCollections.observableArrayList(markets));

        // Disable MarketData until account selectedﬁ
        cbRiskMarketData.setDisable(true);
        setupCombo(cbRiskMarket, MarketMaster::getName);
        // 3) Al cambiar Account → cargamos Markets asociados
        cbRiskAccount.getSelectionModel().selectedItemProperty().addListener((obs, oldAcc, newAcc) -> {
            if (newAcc != null) {
                // find accountId
                Long accId = newAcc.getId();
                // load feeds for that account
                List<FeedDefinition> feeds = catalogService.listFeedsByAccount(accId);
                cbRiskMarketData.setItems(FXCollections.observableArrayList(feeds));
                cbRiskMarketData.setDisable(false);
            } else {
                cbRiskMarketData.getItems().clear();
                cbRiskMarketData.setDisable(true);
            }
        });
        setupCombo(cbRiskMarketData, FeedDefinition::getName);

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
    }

    public void setOnCalculated(Runnable callback) {
        this.onCalculated = callback;
    }

    @FXML
    private void onCalculate() throws IOException {
        if (!validate()) return;
        RiskInputDTO req = buildRequest();
        List<RiskResultDTO> results = riskService.calculate(req);

        // 2) Primer trade?
        new Alert(Alert.AlertType.CONFIRMATION, "¿Es este el primer trade?")
                .showAndWait().filter(ButtonType.OK::equals);

        // 3) Generar XML si el usuario acepta
        if (new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas generar el XML de estrategia?")
                .showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            Path xml = Path.of(System.getProperty("user.home"),
                    marketContext.getMarket().getName() + ".xml");
            exportService.exportStrategyToXml(marketContext, results, xml);
            new Alert(Alert.AlertType.INFORMATION,
                    "XML generado en:\n" + xml).showAndWait();
        }

        // 4) Generar AHK si el usuario acepta
        if (new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas generar el script AutoHotkey?")
                .showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            Path ahk = Path.of(System.getProperty("user.home"),
                    marketContext.getMarket().getName() + ".ahk");
            exportService.exportStrategyToAhk(marketContext, results, ahk);
            new Alert(Alert.AlertType.INFORMATION,
                    "AHK generado en:\n" + ahk).showAndWait();
        }

        // 5) Refrescar la tabla y cerrar el form
        if (onCalculated != null) onCalculated.run();
        dialogStage.close();
    }

    @FXML
    private void onClose() {
        dialogStage.close();
    }

    // ————— Helpers —————

    private <T> void setupCombo(ComboBox<T> combo, java.util.function.Function<T,String> toString) {
        combo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item==null ? null : toString.apply(item));
            }
        });
        combo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item==null ? null : toString.apply(item));
            }
        });
        combo.setConverter(new StringConverter<>() {
            @Override public String toString(T obj) {
                return obj==null ? "" : toString.apply(obj);
            }
            @Override public T fromString(String s) { return null; }
        });
    }

    private boolean validate() {
        if (cbRiskAccount.getValue()==null || cbRiskMarket.getValue()==null || cbRiskMarketData.getValue()==null) {
            alert("Selecciona Account, Market y Market Data."); return false;
        }
        if (!chkHouse.isSelected() && !chkLunch.isSelected()) {
            alert("Selecciona al menos HOUSE o LUNCH."); return false;
        }
        if (!chkWin.isSelected() && !chkLoss.isSelected()) {
            alert("Selecciona al menos WIN o LOSS."); return false;
        }
        try {
            if (Double.parseDouble(tfRiskAccountSize.getText())<=0 ||
                    Double.parseDouble(tfRiskReward.getText())<=0 ||
                    Integer.parseInt(tfTicksSl1.getText())<=0 ||
                    Integer.parseInt(tfTicksSl2.getText())<=0 ) throw new Exception();
        } catch (Exception e) {
            alert("Revisa los valores numéricos."); return false;
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
                .stopLossSize(tfStopLossSize.getText().isEmpty()?0:Integer.parseInt(tfStopLossSize.getText()))
                .house(chkHouse.isSelected())
                .lunch(chkLunch.isSelected())
                .win(chkWin.isSelected())
                .loss(chkLoss.isSelected())
                .build();
    }
}
