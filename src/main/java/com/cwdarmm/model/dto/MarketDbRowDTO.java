package com.cwdarmm.model.dto;

import com.cwdarmm.model.domain.*;
import javafx.beans.property.*;

/**
 * DTO que guarda la entidad completa en un ObjectProperty,
 * y permite bindear en la UI usando el description.
 */
public class MarketDbRowDTO {
    private final ObjectProperty<CatMarket>      future     = new SimpleObjectProperty<>();
    private final ObjectProperty<CatAccount>     account    = new SimpleObjectProperty<>();
    private final ObjectProperty<CatMarketData>  marketData = new SimpleObjectProperty<>();
    private final ObjectProperty<CatContract>    name       = new SimpleObjectProperty<>();
    private final ObjectProperty<CatSymbol>      symbol     = new SimpleObjectProperty<>();

    private final DoubleProperty multiplier  = new SimpleDoubleProperty();
    private final DoubleProperty tickSize    = new SimpleDoubleProperty();
    private final DoubleProperty tickValue   = new SimpleDoubleProperty();
    private final DoubleProperty margin      = new SimpleDoubleProperty();
    private final DoubleProperty commission  = new SimpleDoubleProperty();

    private MarketDbRowDTO() { /* oculto */ }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final MarketDbRowDTO dto = new MarketDbRowDTO();

        public Builder future(CatMarket m)           { dto.future.set(m); return this; }
        public Builder account(CatAccount a)         { dto.account.set(a); return this; }
        public Builder marketData(CatMarketData md)  { dto.marketData.set(md); return this; }
        public Builder name(CatContract c)           { dto.name.set(c); return this; }
        public Builder symbol(CatSymbol s)           { dto.symbol.set(s); return this; }
        public Builder multiplier(double v)          { dto.multiplier.set(v); return this; }
        public Builder tickSize(double v)            { dto.tickSize.set(v); return this; }
        public Builder tickValue(double v)           { dto.tickValue.set(v); return this; }
        public Builder margin(double v)              { dto.margin.set(v); return this; }
        public Builder commission(double v)          { dto.commission.set(v); return this; }

        public MarketDbRowDTO build() {
            return dto;
        }
    }

    // Properties para bindear en la TableView
    public ObjectProperty<CatMarket> getFuture()       { return future; }
    public ObjectProperty<CatAccount> getAccount()     { return account; }
    public ObjectProperty<CatMarketData> getMarketData(){ return marketData; }
    public ObjectProperty<CatContract> getName()       { return name; }
    public ObjectProperty<CatSymbol> getSymbol()       { return symbol; }
    public DoubleProperty getMultiplier()              { return multiplier; }
    public DoubleProperty getTickSize()                { return tickSize; }
    public DoubleProperty getTickValue()               { return tickValue; }
    public DoubleProperty getMargin()                  { return margin; }
    public DoubleProperty getCommission()              { return commission; }
}
