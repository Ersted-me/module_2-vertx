package ru.ersted.util;

public class ParserUtil {
    private ParserUtil() {
    }

    public static int parseIntOrDefault(String value, int defaultValue) {
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
