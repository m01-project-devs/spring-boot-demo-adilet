package com.m01project.taskmanager.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestDBDockerController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TestDBDockerController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/test/db")
    public Map<String, Object> testDb() {
        String sql = "SELECT version()";
        String version = jdbcTemplate.queryForObject(sql, String.class);
        return Map.of("db_version", version);
    }
}
