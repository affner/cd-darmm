package com.cwdarmm.model.dto;

import javafx.beans.property.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class MarketDbRowDTO {

    private final StringProperty future      = new SimpleStringProperty();
    private final StringProperty account     = new SimpleStringProperty();
    private final StringProperty marketData  = new SimpleStringProperty();
    private final StringProperty name        = new SimpleStringProperty();
    private final StringProperty symbol      = new SimpleStringProperty();
    private final DoubleProperty multiplier  = new SimpleDoubleProperty();
    private final DoubleProperty tickSize    = new SimpleDoubleProperty();
    private final DoubleProperty tickValue   = new SimpleDoubleProperty();
    private final DoubleProperty margin      = new SimpleDoubleProperty();
    private final DoubleProperty commission  = new SimpleDoubleProperty();

    public void setFuture(String v)         { future.set(v); }
    public void setAccount(String v)        { account.set(v); }
    public void setMarketData(String v)     { marketData.set(v); }
    public void setName(String v)           { name.set(v); }
    public void setSymbol(String v)         { symbol.set(v); }
    public void setMultiplier(double v)     { multiplier.set(v); }
    public void setTickSize(double v)       { tickSize.set(v); }
    public void setTickValue(double v)      { tickValue.set(v); }
    public void setMargin(double v)         { margin.set(v); }
    public void setCommission(double v)     { commission.set(v); }
}
