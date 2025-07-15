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
    public void exportStrategyToAhk(MarketDTO market, Path target) throws IOException {
        String name = market.getMarket().replace(" ", "_");
        String template =
                "#NoEnv\n" +
                        "#SingleInstance force\n" +
                        "\n" +
                        "; Atajo para seleccionar la estrategia «" + market.getMarket() + "»\n" +
                        "^+s::\n" +
                        "    UIA := UIA_Interface()\n" +
                        "    strat := UIA.ElementFromHandle(\"A\")\n" +
                        "    strat.FindFirstByNameAndType(\"" + market.getMarket() + "\", \"Text\").Click(\"Left\")\n" +
                        "    return\n";
        Files.writeString(target, template);
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
