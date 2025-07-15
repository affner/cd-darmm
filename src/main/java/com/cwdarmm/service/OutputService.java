package com.cwdarmm.service;


import com.cwdarmm.model.dto.RiskResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class  OutputService {

    private static final String HEADER = "TradeNumber,WL,Account,MarketData,AccountSize,RiskKellyA,RiskKellyB";


    public void exportRiskResultsToCsv(List<RiskResultDTO> results, Path outputFile) throws IOException {
        var sb = new StringBuilder();
        sb.append(HEADER).append("\n");
        for (RiskResultDTO r : results) {
            sb.append(r.getTradeNumber()).append(',')
                    .append(r.getWl()).append(',')
                    .append(r.getAccount()).append(',')
                    .append(r.getMarketData()).append(',')
                    .append(r.getAccountSize()).append(',')
                    .append(r.getRiskKellyA()).append(',')
                    .append(r.getRiskKellyB()).append('\n');
        }
        Files.writeString(outputFile, sb.toString(), StandardCharsets.UTF_8);
    }
}
