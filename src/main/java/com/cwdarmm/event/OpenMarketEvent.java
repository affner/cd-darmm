package com.cwdarmm.event;

/**
 *  Emitido por OpenMarketController cuando el usuario pulsa
 *  el botón «OPEN MARKET».  Contiene los datos necesarios para
 *  (a) poblar la hoja MARKETS en tu versión Excel, o
 *  (b) persistir la configuración en tu BD, o
 *  (c) lanzar de inmediato el Risk-Manager, … lo que decidas.
 *
 *  Es un record (Java 17+) muy ligero; si tu proyecto está en 11
 *  cámbialo a una clase con getters.
 */
public record OpenMarketEvent(
        String market,          // “S&P 500”, “GOLD”, …
        String account,         // “NEXGEN”, “APEX”, …
        String marketData,      // “RITHMIC”, …
        double accountSize,     // saldo $
        double riskPctA,        // Risk % inicial A
        double riskPctB         // Risk % inicial B
) {}
