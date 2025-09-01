package com.cwdarmm.controller;

import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.dto.OptimalContractRow;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OptimalContractsController {

    @FXML private Label lblAccount;
    @FXML private HBox boxTables;

    private CatMarket market;

    public void setMarket(CatMarket market) {
        this.market = market;
    }

    public void setCriteriaAccount(String acc) {
        lblAccount.setText(acc);
    }

    public void addTable(List<OptimalContractRow> items, CatMarket market) {
        boxTables.getChildren().add(OptimalContractsPane.build(items, market));
    }

    @FXML
    private void onReset() {
        boxTables.getChildren().clear();
    }
}
