package ru.ersted.util;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.ersted.constant.EventBusAddress;
import ru.ersted.exception.mapper.ExceptionMapper;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventBusConsumerUtil {

    public static <R> void register(
            EventBusAddress address,
            Vertx vertx,
            Function<JsonObject, Future<R>> handler) {

        vertx.eventBus().<JsonObject>consumer(address.name(), msg -> {
            JsonObject body = MessageUtil.requireBody(msg);
            if (body == null) return;

            handler.apply(body)
                    .onSuccess(msg::reply)
                    .onFailure(err ->
                            msg.fail(ExceptionMapper.toInt(err), err.getMessage()));
        });
    }

}
