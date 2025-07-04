package com.cwdarmm.controller;

import com.cwdarmm.event.RiskCalculatedEvent;
import com.cwdarmm.model.domain.Market;
import com.cwdarmm.model.dto.RiskRequestDTO;
import com.cwdarmm.model.domain.RiskResult;
import com.cwdarmm.service.RiskCalcService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RiskManagerController {

    /* ─── servicios ─── */
    private final RiskCalcService riskSvc;
    private final ApplicationEventPublisher pub;

    /* ─── estado ─── */
    private String  marketId;
    private double  tickValue;          // << nuevo: guardamos el tick-value
    private int     tradeCounter = 0;

    /* ─── UI ─── */
    @FXML private ChoiceBox<String> cmbAccount;
    @FXML private ChoiceBox<String> cmbMarket;
    @FXML private ChoiceBox<String> cmbMktData;

    @FXML private TextField txtBalance;
    @FXML private TextField txtRR;
    @FXML private TextField txtStopTickIni;
    @FXML private TextField txtStopTickFin;
    @FXML private TextField txtStopTickStep;

    @FXML private CheckBox chkHOUSE, chkLUNCH, chkWIN, chkLOSS;

    /* listas idem VBA … */
    private static final List<String> ACCOUNTS = List.of(
            "TRADEIFY","TOPSTEP","NEXGEN","NINJA TRADER","TICKTICK TRADER",
            "BLUSKY.PRO","ONE UP TRADER","FUTURES ELITE","APEX",
            "MY FUNDED FUTURES","TAKE PROFIT TRADER");

    private static final List<String> MARKETS = List.of(
            "NASDAQ","S&P 500","GOLD","CRUDE OIL","DOW JONES","MIDCAP",
            "RUSSELL","SILVER","COPPER","HEATING OIL","NATURAL GAS",
            "BRENT CRUDE","R BOB GASOLINE","PLATINUM","PALLADIUM");

    private static final Map<String,List<String>> MKT_DATA =
            Map.ofEntries(
                    Map.entry("TRADEIFY",            List.of("TRADOVATE")),
                    Map.entry("TOPSTEP",             List.of("TRADOVATE","RITHMIC","PROJECTX")),
                    Map.entry("NEXGEN",              List.of("RITHMIC","PROJECTX")),
                    Map.entry("NINJA TRADER",        List.of("PERSONAL")),
                    Map.entry("TICKTICK TRADER",     List.of("TRADOVATE","RITHMIC","PROJECTX")),
                    Map.entry("BLUSKY.PRO",          List.of("TRADOVATE","RITHMIC")),
                    Map.entry("ONE UP TRADER",       List.of("RITHMIC")),
                    Map.entry("FUTURES ELITE",       List.of("PROJECTX","DXFEED")),
                    Map.entry("APEX",                List.of("TRADOVATE","RITHMIC")),
                    Map.entry("MY FUNDED FUTURES",   List.of("TRADOVATE","DXFEED")),
                    Map.entry("TAKE PROFIT TRADER",  List.of("TRADOVATE","RITHMIC"))
            );

    /* ===== 1.  se llama desde MarketTabController ===== */
    public void initWithMarket(Market m) {
        this.marketId  = m.getSymbol();
        this.tickValue = m.getTickValue();      // << guardar valor
    }

    /* ===== 2.  inicialización de los combos ===== */
    @FXML
    private void initialize() {
        cmbAccount.setItems(FXCollections.observableArrayList(ACCOUNTS));
        cmbMarket .setItems(FXCollections.observableArrayList(MARKETS));

        cmbAccount.getSelectionModel().selectedItemProperty().addListener((obs,old,v)->{
            cmbMktData.setItems(FXCollections.observableArrayList(
                    MKT_DATA.getOrDefault(v, List.of())));
            cmbMktData.getSelectionModel().clearSelection();
        });
    }

    /* ===== 3.  botón “OPTIMAL CONTRACTS” ===== */
    @FXML
    private void onOptimalContracts() {

        if(cmbAccount.getValue()==null || cmbMarket.getValue()==null || cmbMktData.getValue()==null){
            alert("Select account, market and market-data first"); return;
        }
        if(anyBlank(txtBalance, txtRR)){
            alert("Fill ACCOUNT SIZE and RISK TO REWARD"); return;
        }

        double stopTicks = txtStopTickIni.getText().isBlank()
                ? 0
                : Double.parseDouble(txtStopTickIni.getText());

        /* construir DTO mínimo – más campos se añadirán cuando
           conectemos todo el cálculo real                              */
        RiskRequestDTO req = RiskRequestDTO.builder()
                .marketId(marketId)
                .accountBalance(Double.parseDouble(txtBalance.getText()))
                .stopTicks(stopTicks)
                .tickValue(tickValue)          // << usamos el valor guardado
                .riskPercentBase(0)
                .lastTradeWin(false)
                .build();

        tradeCounter++;

        RiskResult result = riskSvc.calc(req, tradeCounter,
                com.cwdarmm.model.ConfidenceTier.LOW);

        pub.publishEvent(new RiskCalculatedEvent(marketId, result));
        close();
    }

    @FXML private void onClose(){ close(); }

    /* ===== utilidades ===== */
    private void alert(String msg){ new Alert(Alert.AlertType.ERROR,msg).showAndWait(); }
    private boolean anyBlank(TextField... f){ for (TextField t:f) if(t.getText().isBlank()) return true; return false; }
    private void close(){ ((Stage) txtBalance.getScene().getWindow()).close(); }
}
