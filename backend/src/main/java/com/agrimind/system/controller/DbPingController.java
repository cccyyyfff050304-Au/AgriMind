package com.agrimind.system.controller;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.common.result.Result;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/db")
public class DbPingController {

    private final JdbcTemplate jdbcTemplate;

    public DbPingController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/ping")
    public Result<String> ping() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (Integer.valueOf(1).equals(result)) {
                return Result.success("Database connection is OK");
            }
            throw new BusinessException(500, "Database connection check returned unexpected result");
        } catch (DataAccessException exception) {
            throw new BusinessException(500, "Database connection failed: " + exception.getMostSpecificCause().getMessage());
        }
    }
}
