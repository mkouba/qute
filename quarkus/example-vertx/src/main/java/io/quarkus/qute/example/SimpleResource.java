package io.quarkus.qute.example;

import static io.vertx.core.http.HttpMethod.GET;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.qute.Engine;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.ext.web.Router;

public class SimpleResource {

    @Inject
    Engine engine;

    @Route(path = "/simple", methods = GET, produces = "text/html")
    public void simple(RoutingExchange exchange) {
        Item item = new Item("foo", BigDecimal.ONE);
        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(new Item("bar", new BigDecimal(10)));
        exchange.ok(engine.getTemplate("simple.html").render().putData("item", item).putData("items", items).getResult());
    }

    void addErrorHandler(@Observes Router router) {
        router.errorHandler(500, c -> {
            c.response()
                    .end(c.failure().toString() + "\n" + Arrays.stream(c.failure().getStackTrace()).map(st -> st.toString())
                            .collect(Collectors.joining("\n\t")));
        });
    }

}
