package com.worldanchors.skipapi;

import com.worldanchors.skipapi.database.CustomerDataBaseVerticle;
import com.worldanchors.skipapi.database.ProductsDataBaseVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class SkipApiVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkipApiVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Future<String> customerDbVerticle = Future.future();
        vertx.deployVerticle(new CustomerDataBaseVerticle(), customerDbVerticle.completer());

        customerDbVerticle.compose(customerid -> {

            Future<String> productDbVerticle = Future.future();
            vertx.deployVerticle(new ProductsDataBaseVerticle(), productDbVerticle.completer());

            productDbVerticle.compose(productid -> {
                Future<String> httpVerticleDeployment = Future.future();

                vertx.deployVerticle("com.worldanchors.skipapi.http.HttpServerVerticle",
                        new DeploymentOptions().setInstances(2),
                        httpVerticleDeployment.completer());
                return httpVerticleDeployment;

            }).setHandler(productAsyncResult -> {

                if (productAsyncResult.succeeded()) {
                    customerDbVerticle.complete();

                } else {
                    customerDbVerticle.fail(productAsyncResult.cause());
                    LOGGER.error("Error initializing verticles ", productAsyncResult.cause());
                }
            });

            return productDbVerticle;

        }).setHandler(customerAsyncResult -> {
            if (customerAsyncResult.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(customerAsyncResult.cause());
                LOGGER.error("Error initializing verticles ", customerAsyncResult.cause());
            }
        });

    }

}
