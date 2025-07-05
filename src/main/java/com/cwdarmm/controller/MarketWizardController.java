package com.cwdarmm.controller;

import com.cwdarmm.event.MarketCreatedEvent;
import com.cwdarmm.model.domain.Market;
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
public class MarketWizardController {

    /* servicios ---------------------------------------------------------- */
    private final ApplicationEventPublisher pub;

    /* UI ----------------------------------------------------------------- */
    @FXML private ChoiceBox<String> cmbAccount;
    @FXML private ChoiceBox<String> cmbMarket;
    @FXML private ChoiceBox<String> cmbMktData;

    @FXML private TextField txtAccountSize;
    @FXML private TextField txtRiskA;
    @FXML private TextField txtRiskB;

    @FXML private Button btnOpen;

    /*  datos estáticos  (idénticos al VBA)  ------------------------------ */
    private static final List<String> ACCOUNTS = List.of(
            "TRADEIFY","TOPSTEP","NEXGEN","NINJA TRADER","TICKTICK TRADER",
            "BLUSKY.PRO","ONE UP TRADER","FUTURES ELITE","APEX",
            "MY FUNDED FUTURES","TAKE PROFIT TRADER");

    private static final List<String> MARKETS = List.of(
            "S&P 500","NASDAQ","NASDAQ-100","DOW JONES","GOLD","SILVER","CRUDE OIL");

    private static final Map<String,List<String>> MKT_DATA = Map.ofEntries(
            Map.entry("TRADEIFY",           List.of("TRADOVATE")),
            Map.entry("TOPSTEP",            List.of("TRADOVATE","RITHMIC","PROJECTX")),
            Map.entry("NEXGEN",             List.of("RITHMIC","PROJECTX")),
            Map.entry("NINJA TRADER",       List.of("PERSONAL")),
            Map.entry("TICKTICK TRADER",    List.of("TRADOVATE","RITHMIC","PROJECTX")),
            Map.entry("BLUSKY.PRO",         List.of("TRADOVATE","RITHMIC")),
            Map.entry("ONE UP TRADER",      List.of("RITHMIC")),
            Map.entry("FUTURES ELITE",      List.of("PROJECTX","DXFEED")),
            Map.entry("APEX",               List.of("TRADOVATE","RITHMIC")),
            Map.entry("MY FUNDED FUTURES",  List.of("TRADOVATE","DXFEED")),
            Map.entry("TAKE PROFIT TRADER", List.of("TRADOVATE","RITHMIC"))
    );

    /* ───── init del formulario ───── */
    @FXML
    private void initialize() {

        cmbAccount.setItems(FXCollections.observableArrayList(ACCOUNTS));
        cmbMarket .setItems(FXCollections.observableArrayList(MARKETS));

        cmbAccount.getSelectionModel().selectedItemProperty().addListener((o,old,v)->{
            cmbMktData.setItems(FXCollections.observableArrayList(
                    MKT_DATA.getOrDefault(v, List.of())));
            cmbMktData.getSelectionModel().clearSelection();
        });

        /* deshabilita OPEN MARKET mientras algún campo crítico esté vacío */
        btnOpen.disableProperty().bind(
                cmbAccount.valueProperty().isNull()
                        .or(cmbMarket.valueProperty().isNull())
                        .or(cmbMktData.valueProperty().isNull())
                        .or(txtAccountSize.textProperty().isEmpty())
                        .or(txtRiskA.textProperty().isEmpty())
                        .or(txtRiskB.textProperty().isEmpty()));
    }

    /* ───── botón OPEN MARKET ───── */
    @FXML
    private void onOpenMarket() {

        double size  = parse(txtAccountSize);
        double riskA = parse(txtRiskA);
        double riskB = parse(txtRiskB);

        if (size<=0 || riskA<=0 || riskB<=0){
            alert("Input values must be positive numbers."); return;
        }

        /*  Por ahora sólo creamos un Market mínimo
            - name & symbol se toman del combo Market
            - tick-value lo dejamos fijo según mercado                      */

        Market m = switch(cmbMarket.getValue()){
            case "S&P 500"   -> new Market("S&P 500","ES",0.25,12.5,20,"#98FB98");
            case "NASDAQ"    -> new Market("NASDAQ","NQ",0.25, 5.0,20,"#FFC0CB");
            case "NASDAQ-100"-> new Market("Nasdaq-100","NQ",0.25, 5.0,20,"#FFC0CB");
            case "DOW JONES" -> new Market("DOW JONES","YM",1.00, 5.0,20,"#8EE2FF");
            case "GOLD"      -> new Market("GOLD","GC",0.10,10.0,20,"#FFD700");
            case "SILVER"    -> new Market("SILVER","SI",0.05,25.0,20,"#C0C0C0");
            case "CRUDE OIL" -> new Market("CRUDE OIL","CL",0.01,10.0,20,"#B0E0E6");
            default          -> new Market(cmbMarket.getValue(),"UNK",1,1,20,"#FFFFFF");
        };

        /* <<< Aquí es donde persistirás o invocarás servicios reales       */
        pub.publishEvent(new MarketCreatedEvent(m));

        close();
    }

    @FXML private void onClose(){ close(); }

    /* ───── utilidades ───── */
    private void alert(String msg){ new Alert(Alert.AlertType.ERROR,msg).showAndWait(); }
    private double parse(TextField t){ try { return Double.parseDouble(t.getText()); } catch(Exception e){ return -1;}}
    private void close(){ ((Stage) txtAccountSize.getScene().getWindow()).close(); }
}
