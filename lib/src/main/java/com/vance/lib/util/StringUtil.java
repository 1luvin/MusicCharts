package com.vance.lib.util;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

public class StringUtil {
    public static boolean validateString(String data, List<String> forbiddenParts) {
        final AtomicReference<Boolean> result = new AtomicReference<>(true);
        forbiddenParts.forEach(value -> {
            if (containsIgnoreCase(data, value))
                result.set(false);
        });
        return result.get();
    }

    public static String removeParts(String data, List<String> removableParts) {
        final AtomicReference<String> result = new AtomicReference<>();

        if (data.contains(" - ") && containsIgnoreCase(data, "Remaster"))
            result.set(data.substring(0, data.indexOf('-') - 1));

        removableParts.forEach(value -> {
            if (data.contains(value))
                result.set(data.replace(value, ""));
        });
        return result.get() == null ? data : result.get();
    }
}
