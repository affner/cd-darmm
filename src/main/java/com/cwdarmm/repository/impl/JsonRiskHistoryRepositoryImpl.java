// JsonRiskHistoryRepositoryImpl.java
package com.cwdarmm.repository.impl;

import com.cwdarmm.model.domain.RiskResult;
import com.cwdarmm.repository.RiskHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.nio.file.*;
import java.util.List;
import java.util.Map;

@Repository
public class JsonRiskHistoryRepositoryImpl implements RiskHistoryRepository {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override @SneakyThrows
    public void archiveWeek(int weekNumber, Map<String, List<RiskResult>> data) {
        Path dir = Path.of("history");
        Files.createDirectories(dir);
        Path file = dir.resolve("week-" + weekNumber + ".json");
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(file.toFile(), data);
    }
}
