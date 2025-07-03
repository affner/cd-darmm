package com.cwdarmm.controller;

import com.cwdarmm.event.RiskCalculatedEvent;
import com.cwdarmm.model.dto.RiskRequestDTO;
import com.cwdarmm.model.domain.RiskResult;
import com.cwdarmm.service.RiskCalcService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RiskManagerController {

    private final RiskCalcService riskCalcService;
    private final ApplicationEventPublisher pub;

    private String marketId;
    private int tradeCounter = 0;                 // nº consecutivo

    // --- UI ---
    @FXML private TextField txtBalance;
    @FXML private TextField txtStopTicks;
    @FXML private TextField txtTickValue;
    @FXML private TextField txtRiskPct;
    @FXML private CheckBox chkLastWin;
    @FXML private ChoiceBox<RiskCalcService.ConfidenceTier> cmbConf;

    public void initWithMarket(com.cwdarmm.model.domain.Market m) {
        this.marketId = m.getSymbol();
        txtTickValue.setText(String.valueOf(m.getTickValue()));
    }

    @FXML
    private void onCalculate() {
        // • construye DTO
        RiskRequestDTO req = RiskRequestDTO.builder()
                .marketId(marketId)
                .accountBalance(Double.parseDouble(txtBalance.getText()))
                .stopTicks(Double.parseDouble(txtStopTicks.getText()))
                .tickValue(Double.parseDouble(txtTickValue.getText()))
                .riskPercentBase(Double.parseDouble(txtRiskPct.getText()))
                .lastTradeWin(chkLastWin.isSelected())
                .build();

        tradeCounter++;

        // • llama Servicio
        RiskResult r = riskCalcService.calc(
                req, tradeCounter,
                cmbConf.getValue() == null ? RiskCalcService.ConfidenceTier.LOW
                        : cmbConf.getValue());

        // • publica evento → MarketTab y Results
        pub.publishEvent(new RiskCalculatedEvent(marketId, r));

        // opcional: cerrar auto
        ((javafx.stage.Stage) txtBalance.getScene().getWindow()).close();
    }

    @FXML
    private void onCancel() {
        txtBalance.getScene().getWindow().hide();
    }
}
