package ru.ersted.exception.mapper;

import io.vertx.core.eventbus.ReplyException;
import ru.ersted.exception.BaseApplicationException;
import ru.ersted.exception.BusinessLogicException;
import ru.ersted.exception.DuplicateException;
import ru.ersted.exception.NotFoundException;
import ru.ersted.exception.constant.ExceptionUniqCode;

public final class ExceptionMapper {
    private ExceptionMapper() {
    }

    public static int toInt(Throwable t) {
        return (t instanceof BaseApplicationException ae)
                ? ae.getExceptionUniqCode().getCode()
                : ExceptionUniqCode.UNKNOWN.getCode();
    }

    public static BaseApplicationException from(ReplyException rex) {
        return switch (ExceptionUniqCode.from(rex.failureCode())) {
            case BUSINESS_EXCEPTION_CODE -> new BusinessLogicException(rex.getMessage());
            case NOT_FOUND_EXCEPTION_CODE -> new NotFoundException(rex.getMessage());
            case DUPLICATE_EXCEPTION_CODE -> new DuplicateException(rex.getMessage());
            default -> new BaseApplicationException(rex.getMessage());
        };
    }
}
