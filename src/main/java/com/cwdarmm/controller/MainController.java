package com.cwdarmm.controller;

import com.cwdarmm.event.MarketCreatedEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor              // Spring inyecta los args finales
public class MainController {

    private final ApplicationContext ctx;

    @FXML private TabPane marketsPane;      // <TabPane> del FXML

    /* ---------- Menu actions ---------- */

    @FXML
    private void onExit() {
        // cierra la ventana principal
        marketsPane.getScene().getWindow().hide();
    }

    @FXML
    private void openMarketCatalog() throws Exception {
        // Abre un diálogo modal (MarketCatalog)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/market_catalog.fxml"));
        loader.setControllerFactory(ctx::getBean);
        Region root = loader.load();

        javafx.stage.Stage stage = new javafx.stage.Stage();
        stage.setTitle("Catálogo de Mercados");
        stage.setScene(new javafx.scene.Scene(root));
        stage.initOwner(marketsPane.getScene().getWindow());
        stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
        stage.showAndWait();                 // bloquea hasta cerrar
    }

    /* ---------- EventBus ---------- */

    /**
     * Se dispara cuando MarketService crea un mercado nuevo.
     * → añadimos una pestaña dinámica con su FXML.
     */
    @EventListener
    public void onMarketCreated(MarketCreatedEvent ev) {
        Platform.runLater(() -> addMarketTab(ev.market()));
    }

    /* ---------- helper ---------- */

    private void addMarketTab(com.cwdarmm.model.domain.Market market) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/market_tab.fxml.fxml"));
            loader.setControllerFactory(ctx::getBean);
            Region view = loader.load();

            // pasa la entidad Market al controller de la pestaña
            MarketTabController ctrl = loader.getController();
            ctrl.initWithMarket(market);

            Tab tab = new Tab(market.getName(), view);
            tab.setClosable(true);
            marketsPane.getTabs().add(tab);
            marketsPane.getSelectionModel().select(tab);   // cambia a la nueva
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
