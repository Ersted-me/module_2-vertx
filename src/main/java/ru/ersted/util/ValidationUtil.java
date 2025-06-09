package ru.ersted.util;

import ru.ersted.exception.ValidateException;

public class ValidationUtil {

    private ValidationUtil() {}

    public static void notNull(Object obj, String field) {
        if (obj == null) {
            throw new ValidateException("Field '%s' must not be null".formatted(field));
        }
    }

    public static void positive(long n, String field) {
        if (n <= 0) {
            throw new ValidateException("Field '%s' must be positive".formatted(field));
        }
    }

}
