package ru.ersted.exception;

import lombok.Getter;
import ru.ersted.exception.constant.ExceptionUniqCode;

@Getter
public class BaseApplicationException extends RuntimeException {

    private final ExceptionUniqCode exceptionUniqCode;

    protected BaseApplicationException(ExceptionUniqCode exceptionUniqCode, String message) {
        super(message);
        this.exceptionUniqCode = exceptionUniqCode;
    }

    public BaseApplicationException(String message) {
        this(ExceptionUniqCode.UNKNOWN, message);
    }

}
