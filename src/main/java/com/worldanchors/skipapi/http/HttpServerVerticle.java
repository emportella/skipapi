package com.worldanchors.skipapi.http;

import com.worldanchors.skipapi.database.CustomerDatabaseServices;
import com.worldanchors.skipapi.database.DatabaseServices;
import com.worldanchors.skipapi.database.ProductDatabaseServices;
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
    public static final String CONFIG_CUSTOMER_DB_QUEUE = "skip.customer.queue";
    public static final String CONFIG_PRODUCT_DB_QUEUE = "skip.product.queue";
    private CustomerDatabaseServices customerDBS;
    private ProductDatabaseServices productDBS;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        customerDBS = (CustomerDatabaseServices) DatabaseServices.createProxy(vertx,
                config().getString(CONFIG_CUSTOMER_DB_QUEUE, "skip.customer.queue"));
        productDBS =(ProductDatabaseServices) DatabaseServices.createProxy(vertx,
                config().getString(CONFIG_PRODUCT_DB_QUEUE, "skip.product.queue"));

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        //Customer
        router.route().handler(BodyHandler.create());
        router.post("/api/v1/customer").handler(this::postCustomer);
        //Products
        router.get("/api/v1/product").handler(this::getAllproducts);

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

    private void getAllproducts(RoutingContext routingContext) {
        productDBS.selectAll(asyncResult ->{
           if(asyncResult.succeeded()){
               routingContext.response().putHeader("Content-Type", "application/json; charset=utf-8");
               routingContext.response().end(asyncResult.result().toString());
           } else {
               routingContext.response().setStatusCode(500);
           }
        });
    }

    private void postCustomer(RoutingContext routingContext) {
        customerDBS.insert(routingContext.getBodyAsJson(), asyncResult -> {
            if (asyncResult.succeeded()) {
                routingContext.response().setStatusCode(200);
                routingContext.response().end();
            } else {
                routingContext.response().setStatusCode(400).putHeader("Content-Type",
                        "application/json; charset=utf-8");
                routingContext.response().end("{\"error\":\"There is already an account with this email!\"}");
            }
        });

    }
}
