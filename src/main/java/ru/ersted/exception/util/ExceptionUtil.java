package ru.ersted.exception.util;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.ext.web.RoutingContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.ersted.exception.mapper.ExceptionMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionUtil {

    public static Handler<Throwable> processException(RoutingContext ctx) {
        return exception -> {
            if (exception instanceof ReplyException rex) {
                ctx.fail(ExceptionMapper.from(rex));
            } else {
                ctx.fail(exception);
            }
        };
    }

}
