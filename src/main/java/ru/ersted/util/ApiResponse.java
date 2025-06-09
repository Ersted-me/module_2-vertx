package ru.ersted.util;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public final class ApiResponse {

    private ApiResponse() {
    }

    public static void send(RoutingContext ctx,
                            HttpResponseStatus status,
                            Object body) {

        ctx.response()
                .setStatusCode(status.code())
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end(Json.encode(body));
    }

    public static void noContent(RoutingContext ctx,
                                 HttpResponseStatus status) {

        ctx.response()
                .setStatusCode(status.code())
                .end();
    }

}
