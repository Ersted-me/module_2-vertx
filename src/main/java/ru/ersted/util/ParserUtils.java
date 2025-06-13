package ru.ersted.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.ersted.exception.BadRequestException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParserUtils {

    public static Long toLong(String value) {

        if (value == null || value.isBlank()) {
            throw new BadRequestException("id must be provided");
        }

        if (!value.chars().allMatch(Character::isDigit)) {
            throw new BadRequestException("id must be a positive integer: " + value);
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("id is out of range: " + value);
        }

    }

}
