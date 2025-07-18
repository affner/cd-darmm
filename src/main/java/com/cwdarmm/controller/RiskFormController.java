package com.cwdarmm.controller;

import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.service.ExportService;
import com.cwdarmm.service.RiskService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RiskFormController {
    private final RiskService riskService;

    private final ExportService exportService;
    private Stage dialogStage;
    private Runnable onCalculated;
    private MarketDTO marketContext;

    @FXML
    private ComboBox<String> cbRiskAccount;
    @FXML
    private ComboBox<String> cbRiskMarket;
    @FXML
    private ComboBox<String> cbRiskMarketData;
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
        // Poblar comboboxes al igual que en OpenMarket
        cbRiskAccount.setItems(FXCollections.observableArrayList(riskService.listAccounts()));
        cbRiskMarket.setItems(FXCollections.observableArrayList(riskService.listMarkets()));
        cbRiskMarketData.setItems(FXCollections.observableArrayList(riskService.listMarketData()));

        // Carga imagen de monedas
        imgCoins.setImage(new Image(getClass().getResourceAsStream("/img/coins.png")));
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /**
     * Recibe el MarketDTO del formulario principal.
     */
    public void setMarketContext(MarketDTO context) {
        this.marketContext = context;
        // Pre-popular los combobox o labels según context
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

        // 1) Construir request y calcular
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
                    marketContext.getMarket() + ".xml");
            exportService.exportStrategyToXml(marketContext, results, xml);
            new Alert(Alert.AlertType.INFORMATION,
                    "XML generado en:\n" + xml).showAndWait();
        }

        // 4) Generar AHK si el usuario acepta
        if (new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas generar el script AutoHotkey?")
                .showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            Path ahk = Path.of(System.getProperty("user.home"),
                    marketContext.getMarket() + ".ahk");
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

    private boolean validate() {
        // similar a OpenMarket + validación de checkboxes
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
        // numéricos >0
        try {
            if (Double.parseDouble(tfRiskAccountSize.getText()) <= 0 ||
                    Double.parseDouble(tfRiskReward.getText()) <= 0 ||
                    Integer.parseInt(tfTicksSl1.getText()) <= 0 ||
                    Integer.parseInt(tfTicksSl2.getText()) <= 0)
                throw new NumberFormatException();
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
                .accountSize(Double.parseDouble(tfRiskAccountSize.getText()))
                .riskReward(Double.parseDouble(tfRiskReward.getText()))
                .ticksSl1(Integer.parseInt(tfTicksSl1.getText()))
                .ticksSl2(Integer.parseInt(tfTicksSl2.getText()))
                .stopLossSize(tfStopLossSize.getText().isEmpty() ? 0
                        : Integer.parseInt(tfStopLossSize.getText()))
                .house(chkHouse.isSelected())
                .lunch(chkLunch.isSelected())
                .win(chkWin.isSelected())
                .loss(chkLoss.isSelected())
                .build();
    }

}
