package com.vance.lib.util;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

public class StringUtil {
    private static final AtomicReference<String> dataWithRemovedPart = new AtomicReference<>();
    private static final AtomicReference<Boolean> validationResult = new AtomicReference<>(true);


    public static boolean validateString(String data, List<String> forbiddenParts) {
        validationResult.set(true);
        forbiddenParts.forEach(value -> checkString(data, value));
        return validationResult.get();
    }

    public static String removeParts(String data, List<String> removableParts) {
        dataWithRemovedPart.set(null);

        // special case for Remasters
        if (data.contains(" - ") && containsIgnoreCase(data, "Remaster"))
            dataWithRemovedPart.set(data.substring(0, data.indexOf('-') - 1));

        removableParts.forEach(value -> removePart(data, value));
        return dataWithRemovedPart.get() == null ? data : dataWithRemovedPart.get();
    }

    private static void checkString(String data, String checkedValue) {
        if (containsIgnoreCase(data, checkedValue)) validationResult.set(false);
    }

    private static void removePart(String data, String valueToRemove) {
        if (data.contains(valueToRemove)) dataWithRemovedPart.set(data.replace(valueToRemove, ""));
    }
}
