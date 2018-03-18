package com.worldanchors.skipapi.database;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;

import java.util.stream.Collectors;

public class ProductDatabaseServices implements DatabaseServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDatabaseServices.class);
    private final JDBCClient jdbcClient;

    public ProductDatabaseServices(JDBCClient jdbcClient, Handler<AsyncResult<DatabaseServices>> readyHandler) {
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

        jdbcClient.query("SELECT * FROM skip.product", result -> {
            if (result.succeeded()) {
                JsonArray customers = new JsonArray(result.result()
                        .getResults()
                        .stream().map(json -> json.getString(1))
                        .sorted().collect(Collectors.toList()));
                resultHandle.handle(Future.succeededFuture(customers));
            } else {
                LOGGER.error("Database query error", result.cause());
            }
        });
        return this;
    }

    @Override
    public DatabaseServices selectById(int id, Handler<AsyncResult<JsonObject>> resultHandler) {
        String sqlSelectById = "SELECT * FROM skip.product WHERE id = ? ;";
        jdbcClient.queryWithParams(sqlSelectById, new JsonArray().add(id), fetch -> {
            if (fetch.succeeded()) {
                JsonObject response = new JsonObject();
                ResultSet resultSet = fetch.result();
                if (resultSet.getNumRows() == 0) {
                    response.put("found", false);
                } else {
                    response.put("found", true);
                    JsonArray row = resultSet.getResults().get(0);
                    response.put("product", row.toString());
                }
                resultHandler.handle(Future.succeededFuture(response));
            } else {
                LOGGER.error("Database query error", fetch.cause());
                resultHandler.handle(Future.failedFuture(fetch.cause()));
            }
        });
        return this;
    }


    public DatabaseServices selectByName(String name, Handler<AsyncResult<JsonObject>> resultHandler) {
        String sqlSelectByName = "SELECT * FROM skip.product WHERE name = ? ;";
        jdbcClient.queryWithParams(sqlSelectByName, new JsonArray().add(name), fetch -> {
            if (fetch.succeeded()) {
                JsonObject response = new JsonObject();
                ResultSet resultSet = fetch.result();
                if (resultSet.getNumRows() == 0) {
                    response.put("found", false);
                } else {
                    response.put("found", true);
                    JsonArray row = new JsonArray(resultSet.getResults()
                            .stream().map(json -> json.getInteger(1))
                            .sorted().collect(Collectors.toList())
                    );
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
    public DatabaseServices insert(JsonObject jsonObject, Handler<AsyncResult<Void>> resultHandler) {
        resultHandler.handle(Future.failedFuture(new UnsupportedOperationException("Not supported yet.")));
        return this;
    }

    @Override
    public DatabaseServices update(JsonObject jsonObject, Handler<AsyncResult<Void>> resultHandler) {
        resultHandler.handle(Future.failedFuture(new UnsupportedOperationException("Not supported yet.")));
        return this;
    }

    @Override
    public DatabaseServices delete(int id, Handler<AsyncResult<Void>> resultHandler) {
        resultHandler.handle(Future.failedFuture(new UnsupportedOperationException("Not supported yet.")));
        return this;
    }
}
