package ru.ersted.util;

import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import ru.ersted.exception.mapper.ExceptionMapper;

public final class FutureBusUtil {

    public static <T> void link(Future<T> future, Message<?> msg) {
        future
                .onSuccess(msg::reply)
                .onFailure(err -> msg.fail(ExceptionMapper.toInt(err), err.getMessage()));
    }

}
