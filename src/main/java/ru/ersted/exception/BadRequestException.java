package ru.ersted.exception;

import static ru.ersted.exception.constant.ExceptionUniqCode.BAD_REQUEST_EXCEPTION_CODE;

public class BadRequestException extends BaseApplicationException {

    public BadRequestException(String message) {
        super(BAD_REQUEST_EXCEPTION_CODE, message);
    }

}
