package com.vance.lib.util;

import com.vance.lib.ChartDataProvider;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

public class FileUtil {
    public static final Logger log = getLogger(FileUtil.class);

    /**
     * Reads file from a Resources as a {@code String},
     * basing on {@code ChartDataProvider} class.
     *
     * @param name Name of file
     * @return File data as a {@code String}
     * @throws IOException        if an I/O error occurs reading from the file or a malformed or
     *                            unmappable byte sequence is read
     * @throws OutOfMemoryError   if the file is extremely large, for example larger than {@code 2GB}
     * @throws SecurityException  In the case of the default provider, and a security manager is
     *                            installed, the {@link SecurityManager#checkRead(String) checkRead}
     *                            method is invoked to check read access to the file.
     * @throws URISyntaxException if this URL is not formatted strictly according to RFC2396 and cannot be converted to a URI
     */
    public static String readFile(@NotNull String name) throws IOException, URISyntaxException {
        return readFile(name, ChartDataProvider.class);
    }

    /**
     * Reads file from a Resources as a {@code String},
     * basing on provided class.
     *
     * @param name  Name of file
     * @param clazz Class which would be a base for finding a Resource
     * @return File data as a {@code String}
     * @throws IOException        if an I/O error occurs reading from the file or a malformed or
     *                            unmappable byte sequence is read
     * @throws OutOfMemoryError   if the file is extremely large, for example larger than {@code 2GB}
     * @throws SecurityException  In the case of the default provider, and a security manager is
     *                            installed, the {@link SecurityManager#checkRead(String) checkRead}
     *                            method is invoked to check read access to the file.
     */
    public static String readFile(@NotNull String name, Class<?> clazz) throws IOException {
        log.info("Reading file {}", name);

        final InputStream inputStream = ofNullable(clazz.getResourceAsStream(name))
                .orElseThrow(() -> new IOException("Failed to open file " + name));
        try (inputStream) {
            return IOUtils.toString(inputStream, defaultCharset());
        }
    }
}
