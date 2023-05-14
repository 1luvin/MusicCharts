package com.vance.lib.util;

import com.vance.lib.ChartDataProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileUtil {
    public static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static String readFile(@NotNull String name) throws IOException {
        log.info("Reading file {}", name);

        final InputStream inputStream = Optional.ofNullable(ChartDataProvider.class.getResourceAsStream(name))
                .orElseThrow(() -> new IOException("Failed to open file " + name));
        String result = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
        inputStream.close();
        return result;
    }
}
