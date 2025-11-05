package com.m01project.taskmanager.demo.controller;

import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import java.sql.Connection;

@RestController
public class DbTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/api/test/db")
    public String testDbConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return "Connected to: " + conn.getMetaData().getURL();
        } catch (Exception e) {
            return "Connection failed: " + e.getMessage();
        }
    }
}
