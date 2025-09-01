package com.cwdarmm.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mantiene el porcentaje de riesgo secuencial para cada combinación
 * (account, market, feed, Kelly A/B). Replica el comportamiento de las
 * macros del libro Excel aplicando los factores anti-martingale
 * 1.05 (WIN) y 0.98 (LOSS).
 */
@Service
public class RiskStateService {

    public enum KellyType { A, B }

    private static final BigDecimal FACTOR_WIN  = new BigDecimal("1.05");
    private static final BigDecimal FACTOR_LOSS = new BigDecimal("0.98");

    /** Utilidades de redondeo equivalentes a Excel */
    private static double r2(double v){
        return new BigDecimal(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    public static double r3(double v){
        return new BigDecimal(v).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }
    public static BigDecimal r3(BigDecimal v){
        return v.setScale(3, RoundingMode.HALF_UP);
    }

    private record Key(Long accountId, Long marketId, Long marketDataId, KellyType type){}
    private static class State {
        BigDecimal lastRisk;
        LocalDate lastDate;
        BigDecimal initialRisk;
        State(BigDecimal lastRisk, LocalDate lastDate, BigDecimal initialRisk){
            this.lastRisk = lastRisk;
            this.lastDate = lastDate;
            this.initialRisk = initialRisk;
        }
    }

    private final Map<Key, State> states = new ConcurrentHashMap<>();

    private boolean isNewWeek(LocalDate prev, LocalDate now){
        if(prev == null) return false;
        LocalDate lastMon = prev.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate nowMon  = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return !lastMon.equals(nowMon);
    }

    /** Obtiene el riesgo previo para la clave. Si no existe o cambió la semana,
     * se devuelve el riesgo inicial proporcionado. */
    public BigDecimal getRisk(Long acc, Long mkt, Long feed, KellyType type, BigDecimal initial){
        State st = states.get(new Key(acc,mkt,feed,type));
        LocalDate today = LocalDate.now();
        if(st == null || isNewWeek(st.lastDate, today)){
            return initial;
        }
        return st.lastRisk;
    }

    /** Guarda el riesgo posterior a un trade para la clave. */
    public void updateRisk(Long acc, Long mkt, Long feed, KellyType type, BigDecimal newRisk, BigDecimal initial){
        states.put(new Key(acc,mkt,feed,type), new State(r3(newRisk), LocalDate.now(), initial));
    }

    /** Aplica el resultado de un trade y devuelve el nuevo riesgo. */
    public BigDecimal applyTrade(Long acc, Long mkt, Long feed, KellyType type, boolean win, BigDecimal initial){
        BigDecimal prev = getRisk(acc,mkt,feed,type, initial);
        BigDecimal factor = win ? FACTOR_WIN : FACTOR_LOSS;
        BigDecimal next = r3(prev.multiply(factor));
        updateRisk(acc,mkt,feed,type,next,initial);
        return next;
    }

    /** Limpia todos los estados almacenados. */
    public void reset(){
        states.clear();
    }
}
