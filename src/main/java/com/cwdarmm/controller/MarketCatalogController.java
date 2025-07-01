package com.cwdarmm.controller;

import com.cwdarmm.model.Market;
import com.cwdarmm.repository.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype") // opcional, 1 instancia por carga
public class MarketCatalogController {

    @FXML private TableView<Market> tblMarkets;
    @FXML private TableColumn<Market,String>  colSymbol;
    @FXML private TableColumn<Market,String>  colName;
    @FXML private TableColumn<Market,Double>  colTickSize;
    @FXML private TableColumn<Market,Double>  colTickValue;
    @FXML private TableColumn<Market,Integer> colDefaultStop;

    @Autowired
    private MarketRepository marketRepo;
    private final ObservableList<Market> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colSymbol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSymbol()));
        colName.setCellValueFactory(  c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
        colTickSize.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTickSize()));
        colTickValue.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTickValue()));
        colDefaultStop.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDefaultStop()));

        data.setAll(marketRepo.findAll());
        tblMarkets.setItems(data);
    }

    /* botones crud – vacíos por ahora */
    @FXML private void onAdd()    { /* TODO */ }
    @FXML private void onEdit()   { /* TODO */ }
    @FXML private void onDelete() { /* TODO */ }
}
