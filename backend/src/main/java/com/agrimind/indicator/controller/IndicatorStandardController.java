package com.agrimind.indicator.controller;

import com.agrimind.auth.service.AuthService;
import com.agrimind.common.result.PageResult;
import com.agrimind.common.result.Result;
import com.agrimind.indicator.dto.IndicatorStandardPageQuery;
import com.agrimind.indicator.dto.IndicatorStandardSaveRequest;
import com.agrimind.indicator.service.IndicatorStandardService;
import com.agrimind.indicator.vo.IndicatorStandardVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/indicator-standards")
public class IndicatorStandardController {

    private final IndicatorStandardService standardService;
    private final AuthService authService;

    public IndicatorStandardController(IndicatorStandardService standardService, AuthService authService) {
        this.standardService = standardService;
        this.authService = authService;
    }

    @PostMapping
    public Result<IndicatorStandardVO> create(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody IndicatorStandardSaveRequest request) {
        requireLogin(authorization);
        return Result.success(standardService.create(request));
    }

    @PutMapping("/{id}")
    public Result<IndicatorStandardVO> update(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "指标标准ID必须大于0") @PathVariable Long id,
            @Valid @RequestBody IndicatorStandardSaveRequest request) {
        requireLogin(authorization);
        return Result.success(standardService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "指标标准ID必须大于0") @PathVariable Long id) {
        requireLogin(authorization);
        standardService.delete(id);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<IndicatorStandardVO> detail(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "指标标准ID必须大于0") @PathVariable Long id) {
        requireLogin(authorization);
        return Result.success(standardService.detail(id));
    }

    @GetMapping("/page")
    public Result<PageResult<IndicatorStandardVO>> page(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid IndicatorStandardPageQuery query) {
        requireLogin(authorization);
        return Result.success(standardService.page(query));
    }

    @GetMapping("/list")
    public Result<List<IndicatorStandardVO>> list(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "作物ID必须大于0") @RequestParam(required = false) Long cropId) {
        requireLogin(authorization);
        return Result.success(standardService.list(cropId));
    }

    private void requireLogin(String authorization) {
        authService.getProfile(authorization);
    }
}
