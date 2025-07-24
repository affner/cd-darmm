package com.cwdarmm.service;


import com.cwdarmm.model.dto.RiskResultDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class  OutputService {

    private static final String[] HEADERS = {
            "TradeNumber", "WL", "Account", "MarketData",
            "AccountSize", "RiskKellyA", "RiskKellyB"
    };

    public void exportRiskResultsToCsv(List<RiskResultDTO> results, Path outputFile) throws IOException {
        try (var writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8);
             var printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(HEADERS))) {
            for (RiskResultDTO r : results) {
                printer.printRecord(
                        r.getTradeNumber(),
                        r.getWl(),
                        r.getAccount(),
                        r.getMarketData(),
                        r.getAccountSize(),
                        r.getRiskKellyA(),
                        r.getRiskKellyB()
                );
            }
        }
    }
}
