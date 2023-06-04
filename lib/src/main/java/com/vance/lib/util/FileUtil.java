package com.vance.lib.util;

import com.vance.lib.ChartDataProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

public class FileUtil {
    public static final Logger log = getLogger(FileUtil.class);

    public static String readFile(@NotNull String name) throws IOException {
        log.info("Reading file {}", name);

        final InputStream inputStream = ofNullable(ChartDataProvider.class.getResourceAsStream(name))
                .orElseThrow(() -> new IOException("Failed to open file " + name));
        String result = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(joining());
        inputStream.close();
        return result;
    }

    public static String readFile(@NotNull String name, Class<?> clazz) throws IOException {
        log.info("Reading file {}", name);

        final InputStream inputStream = ofNullable(clazz.getResourceAsStream(name))
                .orElseThrow(() -> new IOException("Failed to open file " + name));
        String result = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(joining());
        inputStream.close();
        return result;
    }
}
