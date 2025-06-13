package ru.ersted.util;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.ersted.exception.BadRequestException;
import ru.ersted.exception.mapper.ExceptionMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtil {

    public static JsonObject requireBody(Message<JsonObject> msg) {

        JsonObject body = msg.body();

        if (body == null) {

            BadRequestException exception = new BadRequestException("Request body is empty or not JSON");

            msg.fail(ExceptionMapper.toInt(exception), exception.getMessage());

            return null;
        }

        return body;
    }

}
