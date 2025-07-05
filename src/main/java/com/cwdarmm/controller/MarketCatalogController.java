package com.cwdarmm.controller;

import com.cwdarmm.model.domain.Market;
import com.cwdarmm.service.MarketService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MarketCatalogController {

    private final MarketService marketService;
    private final ApplicationContext ctx;   // <<-- AÑADE ESTA LÍNEA
    @FXML private TableView<Market> tblMarkets;
    @FXML private Button btnOpen;      // habilitado solo con selección

    @FXML
    private void initialize() {
        // ——— define columnas (puedes declararlas en FXML o aquí) ———
        TableColumn<Market,String> c1 = new TableColumn<>("Name");
        c1.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Market,String> c2 = new TableColumn<>("Symbol");
        c2.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        TableColumn<Market,Double> c3 = new TableColumn<>("Tick");
        c3.setCellValueFactory(new PropertyValueFactory<>("tickValue"));
        tblMarkets.getColumns().setAll(c1,c2,c3);

        // ——— carga datos ———
        List<Market> base = marketService.findAllReferenceMarkets();
        // Si viene vacío, añade una semilla rápida para el MVP
        if (base.isEmpty()) {
            base = List.of(
                    new Market("S&P 500", "ES", 0.25, 12.5, 20, "#FFB347"),
                    new Market("Nasdaq-100", "NQ", 0.25, 5.0,  20, "#7FDBFF"));
        }
        tblMarkets.setItems(FXCollections.observableArrayList(base));

        btnOpen.disableProperty().bind(tblMarkets.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void onOpen() throws Exception {
        Market selected = tblMarkets.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Lanza wizard de 3 pasos (cuenta, riesgo, tamaños ...)
        marketService.openWizardAndCreateMarket(selected);

        // Cierra esta ventana (modal)
        tblMarkets.getScene().getWindow().hide();
    }

    @FXML
    private void onNew() throws Exception {
        // Lanza wizard vacío (usa un Market dummy)
        Market dummy = new Market("","",0,0,20,"#FFFFFF");
        marketService.openWizardAndCreateMarket(dummy);
        ((Stage)tblMarkets.getScene().getWindow()).close();
    }

    /* ------------------------------------------------------------------ */
    /* helper que carga el fxml del wizard */
    private void openWizard(Market base) throws Exception {

        FXMLLoader fx = new FXMLLoader(getClass()
                .getResource("/fxml/market_wizard.fxml"));
        fx.setControllerFactory(ctx::getBean); // si usas Spring
        Parent root = fx.load();

        /* si necesitas pasar el Market base al wizard: */
        MarketWizardController ctrl = fx.getController();
      //  ctrl.prefill(base);              // método opcional (lo ves abajo)

        Stage st = new Stage();
        st.setTitle("Market");
        st.setScene(new Scene(root));
        st.initOwner(tblMarkets.getScene().getWindow());
        st.initModality(Modality.WINDOW_MODAL);
        st.showAndWait();
    }


    @FXML
    private void onCancel() {
        tblMarkets.getScene().getWindow().hide();
    }
}
