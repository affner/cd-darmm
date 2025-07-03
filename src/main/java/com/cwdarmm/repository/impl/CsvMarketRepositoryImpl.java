package com.cwdarmm.repository.impl;

import com.cwdarmm.model.domain.Market;
import com.cwdarmm.repository.MarketRepository;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CsvMarketRepositoryImpl implements MarketRepository {

    private static final Path FILE = Path.of("data", "markets.csv");

//    @Override @SneakyThrows
//    public List<Market> findReferenceCatalog() {
//        if (!Files.exists(FILE)) return List.of();
//        return Files.readAllLines(FILE).stream()
//                .skip(1)                       // header
//                .map(this::parse)
//                .collect(Collectors.toList());
//    }

    @Override
    @SneakyThrows
    public List<Market> findReferenceCatalog() {

        // crea carpeta data/ si no existe
        Files.createDirectories(FILE.getParent());

        // —— semilla automática la primera vez ——
        if (!Files.exists(FILE)) {
            save(new Market("S&P 500",   "ES", 0.25, 12.50, 20, "#FFB347"));
            save(new Market("Nasdaq-100","NQ", 0.25,  5.00, 20, "#7FDBFF"));
        }

        // —— leer archivo completo (incluye cabecera) ——
        return Files.readAllLines(FILE).stream()
                .skip(1)                                  // omite encabezado
                .filter(l -> !l.isBlank())
                .map(this::parse)                         // → Market
                .collect(Collectors.toList());
    }



    @Override @SneakyThrows
    public void save(Market m) {
        Files.createDirectories(FILE.getParent());
        boolean writeHeader = !Files.exists(FILE);
        try (var w = Files.newBufferedWriter(FILE, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            if (writeHeader) w.write("name,symbol,tickSize,tickValue,defaultStop,colorHex\n");
            w.write(String.format("%s,%s,%.2f,%.2f,%d,%s%n",
                    m.getName(), m.getSymbol(), m.getTickSize(), m.getTickValue(),
                    m.getDefaultStop(), m.getColorHex()));
        }
    }

    @Override
    public void archiveWeekAndReset() {
        // para esta versión MVP no se requiere acción extra
    }

    /* ------------ helper ------------ */
    private Market parse(String line) {
        String[] p = line.split(",");
        return new Market(p[0], p[1],
                Double.parseDouble(p[2]),
                Double.parseDouble(p[3]),
                Integer.parseInt(p[4]),
                p[5]);
    }
}
