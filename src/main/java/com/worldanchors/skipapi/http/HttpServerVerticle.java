package com.worldanchors.skipapi.http;

import com.google.gson.Gson;
import com.worldanchors.skipapi.database.CustomerDatabaseServices;
import com.worldanchors.skipapi.database.DatabaseServices;
import com.worldanchors.skipapi.database.Entities.Customer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class HttpServerVerticle extends AbstractVerticle {

    public static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);
    public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
    public static final String CONFIG_DB_QUEUE = "skip.queue";
    private DatabaseServices customerDBS;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        customerDBS = DatabaseServices.createProxy(vertx, config().getString(CONFIG_DB_QUEUE, "skip.queue"));

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/api/v1/customer").handler(this::postCustomer);

        server.requestHandler(router::accept).listen(config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080),
                asyncResult -> {
                    if (asyncResult.succeeded()) {
                        LOGGER.info("Http Server is up and Running on port 8080");
                        startFuture.complete();
                        ;
                    } else {
                        LOGGER.info("Http Server failed to start", asyncResult.cause());
                        startFuture.fail(asyncResult.cause());
                    }
                });

    }

    private void postCustomer(RoutingContext routingContext) {
        Gson gson = new Gson();
        Customer customer = gson.fromJson(routingContext.getBodyAsJson().toString(), Customer.class);
        customerDBS.insert(customer, asyncResult -> {
            if (asyncResult.succeeded()) {
                routingContext.response().setStatusCode(200);
                routingContext.response().end();
            } else {
                routingContext.response().setStatusCode(403).putHeader("Content-Type", "application/json");
                routingContext.response().end("{\"error\":\"The server failed to save the user\"");
            }
        });

    }
}
