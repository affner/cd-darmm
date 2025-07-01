package com.cwdarmm.service;

import com.cwdarmm.model.OptimalContract;
import org.springframework.stereotype.Service;

import java.nio.file.*;

@Service
public class ExportService {

    public void toCsv(OptimalContract oc, Path file) throws Exception {
        String header = "stopTicks,contracts,riskUsd\n";
        String line   = oc.getStopTicks() + "," + oc.getContracts() + "," + oc.getRiskUsd() + "\n";
        Files.writeString(file, header + line);
    }
}
