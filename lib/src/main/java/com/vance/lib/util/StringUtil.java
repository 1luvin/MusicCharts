package com.vance.lib.util;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class StringUtil {


    public static boolean validateString(String data, List<String> forbiddenParts) {
        final AtomicReference<Boolean> result = new AtomicReference<>(true);
        forbiddenParts.forEach(value -> {
            if (data.contains(value))
                result.set(false);
        });
        return result.get();
    }

    public static String removeParts(String data, List<String> removableParts) {
        final AtomicReference<String> result = new AtomicReference<>();
        removableParts.forEach(value -> {
            if (data.contains(value))
                result.set(data.replace(value, ""));
        });
        return result.get() == null ? data : result.get();
    }
}
