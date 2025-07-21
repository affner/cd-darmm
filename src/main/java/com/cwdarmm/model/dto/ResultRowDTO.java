package com.cwdarmm.model.dto;

import javafx.beans.property.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class ResultRowDTO {
    private final StringProperty asset               = new SimpleStringProperty();
    private final StringProperty broker              = new SimpleStringProperty();
    private final StringProperty symbol              = new SimpleStringProperty();
    private final IntegerProperty target             = new SimpleIntegerProperty();
    private final IntegerProperty slSize             = new SimpleIntegerProperty();
    private final DoubleProperty riskPerContract     = new SimpleDoubleProperty();
    private final IntegerProperty optimalContract    = new SimpleIntegerProperty();
    private final DoubleProperty capitalUsed         = new SimpleDoubleProperty();
    private final DoubleProperty realRisk            = new SimpleDoubleProperty();
    private final DoubleProperty potentialProfit     = new SimpleDoubleProperty();
    private final DoubleProperty potentialLoss       = new SimpleDoubleProperty();
    private final DoubleProperty riskPercentage      = new SimpleDoubleProperty();



}
