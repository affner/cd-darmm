package com.cwdarmm.controller;

/**
 * Ventana de consulta de la configuración BD_MARKETS
 * que permite visualizar todos los contratos cargados.
 */

import com.cwdarmm.model.domain.*;
import com.cwdarmm.model.dto.MarketDbRowDTO;
import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.service.MarketDbService;
import com.cwdarmm.service.MarketDataService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BdMarketController {

    @FXML private TableView<MarketDbRowDTO> tblMarketDb;
    @FXML private TableColumn<MarketDbRowDTO, String>  colFuture;
    @FXML private TableColumn<MarketDbRowDTO, String>  colAccount;
    @FXML private TableColumn<MarketDbRowDTO, String>  colMarketData;
    @FXML private TableColumn<MarketDbRowDTO, String>  colName;
    @FXML private TableColumn<MarketDbRowDTO, String>  colSymbol;
    @FXML private TableColumn<MarketDbRowDTO, Double>  colMultiplier;
    @FXML private TableColumn<MarketDbRowDTO, Double>  colTickSize;
    @FXML private TableColumn<MarketDbRowDTO, Double>  colTickValue;
    @FXML private TableColumn<MarketDbRowDTO, Double>  colMargin;
    @FXML private TableColumn<MarketDbRowDTO, Double>  colCommission;

    private final MarketDbService marketDbService;
    private final MarketDataService marketDataService;
    private Stage dialogStage;

    @FXML
    public void initialize() {
        // binding columnas ↔ propiedades DTO
        // Future → mostramos sólo description
        colFuture.setCellValueFactory(cd -> {
            CatMarket m = cd.getValue().getFuture().get();
            return new ReadOnlyStringWrapper(m.getDescription());
        });

        // Account
        colAccount.setCellValueFactory(cd -> {
            CatAccount a = cd.getValue().getAccount().get();
            return new ReadOnlyStringWrapper(a.getDescription());
        });

        // Market Data
        colMarketData.setCellValueFactory(cd -> {
            CatMarketData md = cd.getValue().getMarketData().get();
            return new ReadOnlyStringWrapper(md.getDescription());
        });

        // Contract name
        colName.setCellValueFactory(cd -> {
            CatContract c = cd.getValue().getName().get();
            return new ReadOnlyStringWrapper(c.getDescription());
        });

        // Symbol code
        colSymbol.setCellValueFactory(cd -> {
            CatSymbol s = cd.getValue().getSymbol().get();
            return new ReadOnlyStringWrapper(s.getSymbol());
        });

        // numéricas
        colMultiplier .setCellValueFactory(cd -> cd.getValue().getMultiplier().asObject());
        colTickSize   .setCellValueFactory(cd -> cd.getValue().getTickSize().asObject());
        colTickValue  .setCellValueFactory(cd -> cd.getValue().getTickValue().asObject());
        colMargin     .setCellValueFactory(cd -> cd.getValue().getMargin().asObject());
        colCommission .setCellValueFactory(cd -> cd.getValue().getCommission().asObject());

        // cargamos datos al inicio
        refreshTable();
    }

    public void refreshTable() {
        List<MarketDTO> sessions = marketDataService.findAll();
        List<MarketDbRowDTO> rows;

        if (sessions.isEmpty()) {
            rows = marketDbService.listAll();
        } else {
            rows = new java.util.ArrayList<>();
            for (MarketDTO s : sessions) {
                rows.addAll(
                        marketDbService.listBy(
                                s.getMarket().getId(),
                                s.getAccount().getId(),
                                s.getMarketData().getId()
                        )
                );
            }
        }

        tblMarketDb.setItems(FXCollections.observableArrayList(rows));
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    private void onRefresh() {
        refreshTable();
    }

    @FXML
    private void onClose() {
        if (dialogStage != null) dialogStage.close();
    }
}
