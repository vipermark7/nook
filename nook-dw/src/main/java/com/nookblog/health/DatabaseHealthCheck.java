package com.nookblog.health;

import com.codahale.metrics.health.HealthCheck;

public class DatabaseHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return null;
    }
//    private final Database database;
//
//    public DatabaseHealthCheck(Database database) {
//        this.database = database;
//    }
//
//    @Override
//    protected Result check() throws Exception {
//        if (database.ping()) {
//            return Result.healthy();
//        }
//        return Result.unhealthy("Can't ping database");
//    }
}