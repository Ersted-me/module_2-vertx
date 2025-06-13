package ru.ersted.exception;

import ru.ersted.exception.constant.ExceptionUniqCode;

public class NotFoundException extends BusinessLogicException {

    public NotFoundException(String message) {
        super(ExceptionUniqCode.NOT_FOUND_EXCEPTION_CODE, message);
    }

}
