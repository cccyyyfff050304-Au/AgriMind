package com.agrimind.system.controller;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/db")
public class DbPingController {

    private final DataSource dataSource;

    public DbPingController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/ping")
    public Result<String> ping() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                return Result.success("MySQL connection is ok");
            }
            throw new BusinessException(500, "MySQL connection is invalid");
        } catch (SQLException exception) {
            throw new BusinessException(500, "MySQL connection failed: " + exception.getMessage());
        }
    }
}
