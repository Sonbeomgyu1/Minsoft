package com.mysite.minsoft.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class DatabaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/check-db-connection")
    public String checkDbConnection() {
        try {
            // Execute a query to fetch data from the ADMIN table
            List<Map<String, Object>> adminData = jdbcTemplate.queryForList("SELECT * FROM ADMIN");

            // Construct a string to display the fetched data
            StringBuilder result = new StringBuilder("Connected to the database!\n");
            for (Map<String, Object> row : adminData) {
                result.append("ID: ").append(row.get("ID")).append(", ")
                      .append("Password: ").append(row.get("PASSWORD")).append(", ")
                      .append("Name: ").append(row.get("NAME")).append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            return "Failed to connect to the database: " + e.getMessage();
        }
    }
}
