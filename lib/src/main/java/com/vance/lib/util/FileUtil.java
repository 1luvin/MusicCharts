package com.vance.lib.util;

import com.vance.lib.ChartDataProvider;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileUtil {

    public static String readFile(@NotNull String name) throws IOException {
        final InputStream inputStream = Optional.ofNullable(ChartDataProvider.class.getResourceAsStream(name))
                .orElseThrow(() -> new IOException("Failed to open file " + name));
        String result = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
        inputStream.close();
        return result;
    }
}
