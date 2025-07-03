package com.cwdarmm.event;

import com.cwdarmm.model.domain.Market;

/**
 * Publicado cuando el usuario crea un nuevo mercado.
 * Lo emite MarketService y lo escucha MainController.
 */
public record MarketCreatedEvent(Market market) { }
