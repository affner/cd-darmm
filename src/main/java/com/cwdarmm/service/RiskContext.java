// src/main/java/com/cwdarmm/service/RiskContext.java
package com.cwdarmm.service;

import com.cwdarmm.model.dto.RiskInputDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class RiskContext {
    private final AtomicReference<RiskInputDTO> last = new AtomicReference<>();


    /** Sobrescribe el request actual (puede ser null para limpiar). */
    public void set(RiskInputDTO req) {
        last.set(req);
    }

    /** Devuelve el último request (o null si no hay). */
    public RiskInputDTO get() {
        return last.get();
    }

    /** True si no hay request guardado. */
    public boolean isEmpty() {
        return last.get() == null;
    }

    /** Limpia el request guardado. */
    public void clear() {
        last.set(null);
    }
}
