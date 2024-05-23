package com.mysite.minsoft.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/check-db-connection")
    public String checkDbConnection() {
        try {
            // Execute a simple query to check the database connection
            jdbcTemplate.execute("SELECT 1 FROM DUAL");
            return "Connected to the database!";
        } catch (Exception e) {
            return "Failed to connect to the database: " + e.getMessage();
        }
    }
}
