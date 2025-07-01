package com.cwdarmm.controller;

import com.cwdarmm.model.Market;
import com.cwdarmm.model.OptimalContract;
import com.cwdarmm.model.RiskProfile;
import com.cwdarmm.model.TradingAccount;
import com.cwdarmm.repository.AccountRepository;
import com.cwdarmm.repository.MarketRepository;
import com.cwdarmm.service.RiskEngine;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype") // opcional, 1 instancia por carga
public class RiskManagerController {

    @FXML private ComboBox<Market> cboMarket;
    @FXML private ComboBox<TradingAccount> cboAccount;
    @FXML private TextField txtStopTicks;
    @FXML private Label lblContracts;

    @FXML private TableView<OptimalContract> tblResults;
    @FXML private TableColumn<OptimalContract,Integer> colStop;
    @FXML private TableColumn<OptimalContract,Integer> colQty;
    @FXML private TableColumn<OptimalContract,Double> colRisk;

    /* Repos básicos (CSV) */
    @Autowired
    private MarketRepository marketRepo;
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private RiskEngine engine;    // KellySizer se define como @Service
    private final ObservableList<OptimalContract> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cboMarket.setItems(FXCollections.observableArrayList(marketRepo.findAll()));
        cboAccount.setItems(FXCollections.observableArrayList(accountRepo.findAll()));

        colStop.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStopTicks()));
        colQty.setCellValueFactory( c -> new ReadOnlyObjectWrapper<>(c.getValue().getContracts()));
        colRisk.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getRiskUsd()));
        tblResults.setItems(data);
    }

    @FXML
    private void onCalculate() {
        Market m  = cboMarket.getValue();
        TradingAccount acc = cboAccount.getValue();
        if (m == null || acc == null) return;

        int stopTicks = Integer.parseInt(txtStopTicks.getText());
        RiskProfile rp = new RiskProfile( /* kellyA */ 2.5, /* kellyB */ 1.5, 1.05, 0.98);

        OptimalContract oc = engine.calculate(m, acc, rp, stopTicks);
        lblContracts.setText(String.valueOf(oc.getContracts()));

        data.clear();
        data.add(oc);
    }
}
