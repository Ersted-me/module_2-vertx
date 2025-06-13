package ru.ersted.exception.constant;

import lombok.Getter;

@Getter
public enum ExceptionUniqCode {
    UNKNOWN(0),

    BUSINESS_EXCEPTION_CODE(1),

    NOT_FOUND_EXCEPTION_CODE(2),

    DUPLICATE_EXCEPTION_CODE(3),

    BAD_REQUEST_EXCEPTION_CODE(4),
    ;


    private final int code;

    ExceptionUniqCode(int code) {
        this.code = code;
    }

    public static ExceptionUniqCode from(int code) {
        for (ExceptionUniqCode ec : values())
            if (ec.code == code) return ec;
        return UNKNOWN;
    }

}
