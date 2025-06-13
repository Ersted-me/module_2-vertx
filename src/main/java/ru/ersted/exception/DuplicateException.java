package ru.ersted.exception;

import ru.ersted.exception.constant.ExceptionUniqCode;

public class DuplicateException extends BusinessLogicException {

    public DuplicateException(String message) {
        super(ExceptionUniqCode.DUPLICATE_EXCEPTION_CODE, message);
    }

}
