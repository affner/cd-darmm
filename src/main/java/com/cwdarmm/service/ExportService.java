package com.cwdarmm.service;

import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.model.dto.MarketDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    /**
     * Crea un XML tipo NinjaTrader/AtmStrategy con un nodo <Bracket> por cada RiskResultDTO.
     */
    public void exportStrategyToXml(MarketDTO market, List<RiskResultDTO> results, Path target) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<NinjaTrader>\n");
        sb.append("  <AtmStrategy\n");
        sb.append("      xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n");
        sb.append("      Name=\"").append(market.getMarket()).append("\"\n");
        sb.append("      Account=\"").append(market.getAccount()).append("\"\n");
        sb.append("      DataFeed=\"").append(market.getMarketData()).append("\">\n");
        sb.append("    <Brackets>\n");
        for (RiskResultDTO r : results) {
            sb.append("      <Bracket>\n");
            sb.append("        <Quantity>").append(determineQuantity(r)).append("</Quantity>\n");
            sb.append("        <StopLoss>").append(r.getRiskKellyA()).append("</StopLoss>\n");
            sb.append("        <Target>").append(r.getRiskKellyB()).append("</Target>\n");
            sb.append("      </Bracket>\n");
        }
        sb.append("    </Brackets>\n");
        sb.append("  </AtmStrategy>\n");
        sb.append("</NinjaTrader>\n");
        Files.writeString(target, sb.toString());
    }

    /**
     * Genera un script AutoHotkey para seleccionar la estrategia en NinjaTrader.
     */
    public void exportStrategyToAhk(MarketDTO market, List<RiskResultDTO> results, Path target) throws IOException {
        // Reemplaza espacios para usar en nombre interno si es necesario
        StringBuilder sb = new StringBuilder();

        // Encabezado del script AHK
        sb.append("#NoEnv").append("\n");
        sb.append("#SingleInstance force").append("\n\n");

        // Comentario con el nombre de la estrategia
        sb.append("; Script generado para estrategia «")
                .append(market.getMarket())
                .append("»\n");

        // Hotkey principal (Ctrl+Shift+S) para abrir la selección de estrategia
        sb.append("^+s::\n");
        sb.append("    UIA := UIA_Interface()\n");
        sb.append("    strat := UIA.ElementFromHandle(\"A\")\n");
        sb.append("    strat.FindFirstByNameAndType(\"")
                .append(market.getMarket())
                .append("\", \"Text\").Click(\"Left\")\n");
        sb.append("    return\n\n");

        // Opcional: generar atajos adicionales por cada trade calculado
        for (RiskResultDTO r : results) {
            sb.append("; Trade #").append(r.getTradeNumber())
                    .append(" – ").append(r.getWl()).append("\n");
            sb.append("^+").append(r.getTradeNumber()).append("::\n");
            sb.append("    ; Aquí pones la secuencia para el trade ")
                    .append(r.getTradeNumber()).append("\n");
            sb.append("    return\n\n");
        }

        // Escribir fichero
        Files.writeString(target, sb.toString());
    }

    /**
     * Ejemplo de lógica para determinar cantidad de contratos.
     * (puedes reemplazarlo con la lógica real del .bas)
     */
    private int determineQuantity(RiskResultDTO r) {
        // placeholder: siempre 1 contrato
        return 1;
    }
}
