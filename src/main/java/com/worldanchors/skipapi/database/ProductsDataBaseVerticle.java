package com.worldanchors.skipapi.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.serviceproxy.ServiceBinder;

public class ProductsDataBaseVerticle extends AbstractVerticle {

    public static final String CONFIG_JDBC_URL = "skip.jdbc.url";
    public static final String CONFIG_JDBC_DRIVER_CLASS = "skip.jdbc.driver_class";
    public static final String CONFIG_JDBC_USER = "skip.jdbc.user";
    public static final String CONFIG_JDBC_PASSWORD = "skip.jdbc.password";
    public static final String CONFIG_JDBC_MAX_POOL_SIZE = "skip.jdbc.max_pool_size";
    public static final String CONFIG_JDBC_SHARED_POOL_NAME = "skip_pool";
    public static final String CONFIG_QUEUE = "skip.product.queue";


    @Override
    public void start(Future<Void> startFuture) throws Exception {
        JDBCClient jdbcClient = JDBCClient.createShared(vertx, new JsonObject()
        .put("url", config().getString(CONFIG_JDBC_URL,"jdbc:mysql://localhost:3306/skip?zeroDateTimeBehavior=convertToNull"))
        .put("driver", config().getString(CONFIG_JDBC_DRIVER_CLASS, "com.mysql.jdbc.Driver"))
        .put("user", config().getString(CONFIG_JDBC_USER, "root"))
        .put("password", config().getString(CONFIG_JDBC_PASSWORD, "besta77"))
        .put("max_pool_size", config().getInteger(CONFIG_JDBC_MAX_POOL_SIZE, 30)),
                config().getString(CONFIG_JDBC_SHARED_POOL_NAME, "skip.product.pool")
        );

        DatabaseServices.create(ProductDatabaseServices.class.getName(),jdbcClient, ready ->{
            if(ready.succeeded()){
                ServiceBinder binder = new ServiceBinder(vertx);
                binder.setAddress(CONFIG_QUEUE).register(DatabaseServices.class, ready.result());
            }else{
                startFuture.fail(ready.cause());
            }
        });
    }
}
