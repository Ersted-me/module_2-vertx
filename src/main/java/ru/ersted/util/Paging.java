package ru.ersted.util;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import ru.ersted.exception.BadRequestException;

public record Paging(int limit, int offset) {

    public static final int DEFAULT_LIMIT = 20;
    public static final int DEFAULT_OFFSET = 0;

    public static Paging fromQuery(RoutingContext ctx) {

        int limit = parseInt(ctx.queryParam("limit").stream().findFirst().orElse(null), DEFAULT_LIMIT);
        int offset = parseInt(ctx.queryParam("offset").stream().findFirst().orElse(null), DEFAULT_OFFSET);

        if (limit < 1 || offset < 0) {
            throw new BadRequestException("limit must be >= 1, offset >= 0");
        }

        return new Paging(limit, offset);

    }

    private static int parseInt(String raw, int def) {

        if (raw == null || raw.isBlank()) return def;

        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("limit/offset must be integers");
        }

    }

    public JsonObject toJson() {
        return new io.vertx.core.json.JsonObject()
                .put("limit", limit)
                .put("offset", offset);
    }

}
