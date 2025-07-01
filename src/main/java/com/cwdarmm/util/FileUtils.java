package com.cwdarmm.util;

import java.nio.file.*;

public final class FileUtils {

    private FileUtils(){}

    public static Path ensureDir(Path path) throws Exception {
        return Files.exists(path) ? path : Files.createDirectories(path);
    }
}
