package ru.ersted.exception.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import ru.ersted.exception.BadRequestException;
import ru.ersted.exception.DuplicateException;
import ru.ersted.exception.NotFoundException;
import ru.ersted.module_2vertx.dto.generated.ExceptionResponse;
import ru.ersted.util.ApiResponse;

import java.time.OffsetDateTime;

@Slf4j
public class GlobalErrorHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext event) {
        Throwable cause = event.failure();

        HttpResponseStatus status;

        if (cause instanceof BadRequestException) {
            status = HttpResponseStatus.BAD_REQUEST;
        } else if (cause instanceof NotFoundException) {
            status = HttpResponseStatus.NOT_FOUND;
        } else if (cause instanceof DuplicateException) {
            status = HttpResponseStatus.CONFLICT;
        } else {
            log.error("Unhandled exception", cause);
            status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        }

        ApiResponse.send(event, status, new ExceptionResponse()
                .timestamp(OffsetDateTime.now())
                .status(status.code())
                .error(status.reasonPhrase())
                .message(cause.getMessage())
                .path(event.request().path()));

    }

}
