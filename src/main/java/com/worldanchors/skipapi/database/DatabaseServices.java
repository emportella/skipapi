package com.worldanchors.skipapi.database;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

@ProxyGen
public interface DatabaseServices {


    static DatabaseServices create(JDBCClient dbClient, Handler<AsyncResult<DatabaseServices>> readyHandler) {
        return new CustomerDatabaseServices(dbClient, readyHandler);
    }

    static DatabaseServices createProxy(Vertx vertx, String address) {
        return new DatabaseServicesVertxEBProxy(vertx, address);
    }

    @Fluent
    DatabaseServices selectAll(Handler<AsyncResult<JsonArray>> resultHandle);

    @Fluent
    DatabaseServices selectById(int id, Handler<AsyncResult<JsonObject>> resultHandler);

    @Fluent
    DatabaseServices insert(T entity, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    DatabaseServices update(T entity, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    DatabaseServices delete(int id, Handler<AsyncResult<Void>> resultHandler);

}
