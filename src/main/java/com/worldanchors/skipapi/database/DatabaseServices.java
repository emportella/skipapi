package com.worldanchors.skipapi.database;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@ProxyGen
public interface DatabaseServices {


    static Object create(String className, JDBCClient dbClient, Handler<AsyncResult<DatabaseServices>> readyHandler) throws Exception {
        Class<?> aClass = Class.forName(className);
        Constructor<?> constructor = aClass.getConstructor(Vertx.class, String.class);
        return constructor.newInstance(dbClient, readyHandler);
    }

    static DatabaseServices createProxy(Vertx vertx, String address) {
        return new DatabaseServiceVertxEBProxy(vertx, address);

    }

    @Fluent
    DatabaseServices selectAll(Handler<AsyncResult<JsonArray>> resultHandle);

    @Fluent
    DatabaseServices selectById(int id, Handler<AsyncResult<JsonObject>> resultHandler);

    @Fluent
    DatabaseServices insert(JsonObject jsonObject, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    DatabaseServices update(JsonObject jsonObject, Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    DatabaseServices delete(int id, Handler<AsyncResult<Void>> resultHandler);

}
