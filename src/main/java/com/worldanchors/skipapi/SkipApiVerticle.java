package com.worldanchors.skipapi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;

public class SkipApiVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkipApiVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Future<String> dbVerticle = Future.future();
        dbVerticle.compose(id -> {
            Future<String> httpVerticleDeployment = Future.future();

            vertx.deployVerticle("com.worldanchors.skipapi.http.HttpServerVerticle",
                    new DeploymentOptions().setInstances(2),
                    httpVerticleDeployment.completer());

            return httpVerticleDeployment;


        }).setHandler(asyncResult -> {
            if (asyncResult.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(asyncResult.cause());
            }
        });

    }

}
