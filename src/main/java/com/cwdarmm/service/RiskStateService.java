package com.cwdarmm.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio encargado de mantener el estado del porcentaje de riesgo para cada
 * combinación (account, market, marketData, kellyType). Replica el algoritmo
 * anti-martingale del libro Excel/VBA.
 */
@Service
public class RiskStateService {

    public static final double FACTOR_WIN  = 1.05;
    public static final double FACTOR_LOSS = 0.98;

    private static final WeekFields WF = WeekFields.of(Locale.getDefault());

    private final Map<StateKey, State> states = new ConcurrentHashMap<>();

    /** Devuelve el último porcentaje de riesgo conocido para la clave indicada.
     *  Si no existe o cambió de semana, devuelve e inicializa con el porcentaje
     *  inicial proporcionado. */
    public synchronized double getLastRisk(Long accountId,
                                           Long marketId,
                                           Long marketDataId,
                                           KellyType type,
                                           double initialRiskPct) {
        StateKey key = new StateKey(accountId, marketId, marketDataId, type);
        State state = states.get(key);
        LocalDate today = LocalDate.now();
        if (state == null || isNewWeek(state.lastDate, today)) {
            double init = ExcelRounding.r3(initialRiskPct);
            states.put(key, new State(init, today));
            return init;
        }
        return state.lastRiskPct;
    }

    /**
     * Aplica el resultado del trade (WIN/LOSS) y devuelve el nuevo porcentaje
     * de riesgo, almacenándolo para futuras consultas.
     */
    public synchronized double applyTrade(Long accountId,
                                          Long marketId,
                                          Long marketDataId,
                                          KellyType type,
                                          double initialRiskPct,
                                          boolean win) {
        double prev = getLastRisk(accountId, marketId, marketDataId, type, initialRiskPct);
        double factor = win ? FACTOR_WIN : FACTOR_LOSS;
        double next = ExcelRounding.r3(prev * factor);
        states.put(new StateKey(accountId, marketId, marketDataId, type),
                new State(next, LocalDate.now()));
        return next;
    }

    /** Guarda explícitamente un porcentaje de riesgo como último valor. */
    public synchronized void save(Long accountId,
                                  Long marketId,
                                  Long marketDataId,
                                  KellyType type,
                                  double riskPct) {
        states.put(new StateKey(accountId, marketId, marketDataId, type),
                new State(ExcelRounding.r3(riskPct), LocalDate.now()));
    }

    /** Reinicia el estado para la clave indicada con el valor inicial. */
    public synchronized void reset(Long accountId,
                                   Long marketId,
                                   Long marketDataId,
                                   KellyType type,
                                   double initialRiskPct) {
        double init = ExcelRounding.r3(initialRiskPct);
        states.put(new StateKey(accountId, marketId, marketDataId, type),
                new State(init, LocalDate.now()));
    }

    private boolean isNewWeek(LocalDate prev, LocalDate now) {
        if (prev == null) return true;
        int w1 = prev.get(WF.weekOfWeekBasedYear());
        int w2 = now.get(WF.weekOfWeekBasedYear());
        int y1 = prev.get(WF.weekBasedYear());
        int y2 = now.get(WF.weekBasedYear());
        return w1 != w2 || y1 != y2;
    }

    private record State(double lastRiskPct, LocalDate lastDate) {}

    private record StateKey(Long accountId, Long marketId, Long marketDataId, KellyType type) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StateKey)) return false;
            StateKey other = (StateKey) o;
            return Objects.equals(accountId, other.accountId)
                    && Objects.equals(marketId, other.marketId)
                    && Objects.equals(marketDataId, other.marketDataId)
                    && type == other.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(accountId, marketId, marketDataId, type);
        }
    }
}
