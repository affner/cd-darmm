package com.cwdarmm.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utilidades de redondeo que replican exactamente el comportamiento de Excel.
 * <p>
 *  - {@link #r3(double)} redondea a tres decimales con HALF_UP.
 *  - {@link #r2(double)} redondea a dos decimales con HALF_UP.
 * </p>
 */
public final class ExcelRounding {
    private ExcelRounding() {}

    /** Redondeo a dos decimales (monto en USD por contrato). */
    public static double r2(double v) {
        return new BigDecimal(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /** Redondeo a tres decimales (porcentaje de riesgo). */
    public static double r3(double v) {
        return new BigDecimal(v).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }
}
