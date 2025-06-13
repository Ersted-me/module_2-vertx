package ru.ersted.exception;

import ru.ersted.exception.constant.ExceptionUniqCode;

public class BusinessLogicException extends BaseApplicationException {

    public BusinessLogicException(ExceptionUniqCode exceptionUniqCode, String message) {
        super(exceptionUniqCode, message);
    }

    public BusinessLogicException(String message) {
        super(ExceptionUniqCode.BUSINESS_EXCEPTION_CODE, message);
    }

}
