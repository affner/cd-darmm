package com.cwdarmm.controller;

import com.cwdarmm.model.dto.OptimalContractRow;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OptimalContractsController {

    @FXML private Label lblAccount;
    @FXML private TableView<OptimalContractRow> tblOptimal;
    @FXML private TableColumn<OptimalContractRow,Integer> colSlSize;
    @FXML private TableColumn<OptimalContractRow,String>  colTicker;
    @FXML private TableColumn<OptimalContractRow, BigDecimal> colOptimal;
    @FXML private TableColumn<OptimalContractRow,Integer> colTarget;

    @FXML
    public void initialize() {
        colSlSize .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSlSize()));
        colTicker .setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getFuturesTicker()));
        colOptimal.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getOptimalContract()));
        colTarget .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTargetTicks()));
    }

    public void setCriteriaAccount(String acc) {
        lblAccount.setText(acc);
    }

    public void setItems(List<OptimalContractRow> items) {
        tblOptimal.setItems(FXCollections.observableArrayList(items));
    }
}
