package com.cwdarmm.controller;

import com.cwdarmm.event.RiskCalculatedEvent;
import com.cwdarmm.service.ResultsAggregatorService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ResultsController {

    private final ResultsAggregatorService agg;

    @FXML private Label lblTotalCapital;

    @FXML
    private void initialize() {
        refresh();
    }

    @EventListener
    public void onRisk(RiskCalculatedEvent e) { refreshAsync(); }

    private void refreshAsync() {
        Platform.runLater(this::refresh);
    }

    private void refresh() {
        lblTotalCapital.setText(String.format("$%,.2f", agg.getTotalCapital()));
    }
}
