package com.worldanchors.skipapi.database;

import com.worldanchors.skipapi.database.Entities.Customer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

import java.util.stream.Collectors;

public class CustomerDatabaseServices implements DatabaseServices<Customer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDatabaseServices.class);
    private final JDBCClient jdbcClient;

    public CustomerDatabaseServices(JDBCClient jdbcClient, Handler<AsyncResult<DatabaseServices>> readyHandler) {
        this.jdbcClient = jdbcClient;

        jdbcClient.getConnection(asyncResult -> {
            if (asyncResult.failed()) {
                LOGGER.error("Could not open a database connection", asyncResult.cause());
                readyHandler.handle(Future.failedFuture(asyncResult.cause()));
            } else {
                readyHandler.handle(Future.succeededFuture(this));
            }
        });
    }

    @Override
    public DatabaseServices selectAll(Handler<AsyncResult<JsonArray>> resultHandle) {
        jdbcClient.query("SELECT * FROM customer", result -> {
            if (result.succeeded()) {
                JsonArray customers = new JsonArray(result.result()
                        .getResults()
                        .stream()
                        .map(json -> json.getString(0))
                        .sorted()
                        .collect(Collectors.toList()));
                resultHandle.handle(Future.succeededFuture(customers));
            } else {
                LOGGER.error("Database query error", result.cause());
            }
        });
        return this;
    }


    @Override
    public DatabaseServices selectById(int id, Handler<AsyncResult<JsonObject>> resultHandler) {
        jdbcClient.queryWithParams("SELECT * FROM customer WHERE id = ? ;", new JsonArray().add(id), fetch -> {
            if (fetch.succeeded()) {
                JsonObject response = new JsonObject();
                ResultSet resultSet = fetch.result();
                if (resultSet.getNumRows() == 0) {
                    response.put("found", false);
                } else {
                    response.put("found", true);
                    JsonArray row = resultSet.getResults().get(0);
                    response.put("id", row.getInteger(0));
                    response.put("rawContent", row.getString(1));
                }
                resultHandler.handle(Future.succeededFuture(response));
            } else {
                LOGGER.error("Database query error", fetch.cause());
                resultHandler.handle(Future.failedFuture(fetch.cause()));
            }
        });
        return this;
    }

    @Override
    public DatabaseServices insert(Customer entity, Handler<AsyncResult<Void>> resultHandler) {
        return null;
    }

    @Override
    public DatabaseServices update(Customer entity, Handler<AsyncResult<Void>> resultHandler) {
        return null;
    }

    @Override
    public DatabaseServices delete(int id, Handler<AsyncResult<Void>> resultHandler) {
        return null;
    }


}
