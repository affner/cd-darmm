package com.cwdarmm.controller;

import com.cwdarmm.event.OpenMarketEvent;
import com.cwdarmm.model.domain.Market;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OpenMarketController {

    private final ApplicationEventPublisher pub;   // enviará MarketCreatedEvent
    /* ---------- resultado que el servicio leerá ---------- */
    @Getter
    private Market result;        // null si se pulsa Cancel
    /* ── UI ── */
    @FXML private ChoiceBox<String> cmbAccount;
    @FXML private ChoiceBox<String> cmbMarket;
    @FXML private ChoiceBox<String> cmbMktData;

    @FXML private TextField txtBalance;
    @FXML private TextField txtRiskA;
    @FXML private TextField txtRiskB;

    /* listas fijas (las mismas que en Risk-Manager) */
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

    @FXML
    private void initialize() {
        cmbAccount.setItems(FXCollections.observableArrayList(ACCOUNTS));
        cmbMarket .setItems(FXCollections.observableArrayList(MARKETS));

        cmbAccount.getSelectionModel().selectedItemProperty().addListener((o,old,v)-> {
            cmbMktData.setItems(FXCollections.observableArrayList(
                    MKT_DATA.getOrDefault(v, List.of())));
            cmbMktData.getSelectionModel().clearSelection();
        });
    }

    /* ── OK ── */
    @FXML
    private void onOpenMarket() {

        if (cmbAccount.getValue()==null || cmbMarket.getValue()==null || cmbMktData.getValue()==null){
            alert("Select Account / Market / Market-Data"); return;
        }
        if (txtBalance.getText().isBlank() || txtRiskA.getText().isBlank() || txtRiskB.getText().isBlank()){
            alert("Fill numeric fields"); return;
        }

        /* Aquí solo publicamos un evento simplificado con los datos
           (puedes crear tu propio DTO u objeto de dominio)           */
        pub.publishEvent(new OpenMarketEvent(
                cmbMarket.getValue(),
                cmbAccount.getValue(),
                cmbMktData.getValue(),
                Double.parseDouble(txtBalance.getText()),
                Double.parseDouble(txtRiskA.getText()),
                Double.parseDouble(txtRiskB.getText())
        ));
        close();
    }

    @FXML private void onClose(){ close(); }

    /* =================================================================
     1.  Este método lo llama FxWizardService inmediatamente después
         de cargar el FXML. Simplemente rellena el formulario si
         viene un Market base (puede ser dummy o real).
     ================================================================= */
    public void prefill(Market base) {
        if (base == null) return;

        // Ejemplo de precarga mínima:
        cmbMarket.setValue(base.getName());
        // si tuvieras combos de símbolo, tickValue, etc., también aquí
    }

    /* util */
    private void alert(String msg){ new Alert(Alert.AlertType.ERROR,msg).showAndWait(); }
    private void close(){ ((Stage) txtBalance.getScene().getWindow()).close(); }
}
