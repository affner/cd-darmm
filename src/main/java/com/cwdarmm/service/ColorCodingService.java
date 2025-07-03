package com.cwdarmm.service;

import com.cwdarmm.model.domain.Market;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Service;

@Service
public class ColorCodingService {

    public Color uiColor(Market market) {
        return Color.web(market.getColorHex(), 1.0);
    }
}
