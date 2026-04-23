package com.example.dockerlab.web;

import java.sql.Connection;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbPingController {

    private final DataSource dataSource;

    public DbPingController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/api/db-ping")
    public Map<String, Object> dbPing() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            var meta = connection.getMetaData();
            try (var statement = connection.createStatement();
                    var rs = statement.executeQuery("SELECT 1")) {
                rs.next();
                return Map.of(
                        "connected", true,
                        "databaseProductName", meta.getDatabaseProductName(),
                        "databaseProductVersion", meta.getDatabaseProductVersion());
            }
        }
    }
}
