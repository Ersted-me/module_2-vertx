package ru.ersted.util;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import ru.ersted.exception.BadRequestException;

public class RequestUtil {

    public static JsonObject requireJsonBody(RoutingContext ctx) {

        JsonObject json = ctx.body().asJsonObject();

        if (json == null) {
            throw new BadRequestException("Request body is empty or not JSON");
        }

        return json;
    }

}
