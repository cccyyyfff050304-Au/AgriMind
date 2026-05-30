package com.agrimind.soil.controller;

import com.agrimind.auth.service.AuthService;
import com.agrimind.common.result.PageResult;
import com.agrimind.common.result.Result;
import com.agrimind.soil.dto.SoilRecordPageQuery;
import com.agrimind.soil.dto.SoilRecordSaveRequest;
import com.agrimind.soil.service.SoilRecordService;
import com.agrimind.soil.vo.SoilRecordVO;
import com.agrimind.user.vo.UserProfileVO;
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
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/soil-records")
public class SoilRecordController {

    private final SoilRecordService soilRecordService;
    private final AuthService authService;

    public SoilRecordController(SoilRecordService soilRecordService, AuthService authService) {
        this.soilRecordService = soilRecordService;
        this.authService = authService;
    }

    @PostMapping
    public Result<SoilRecordVO> create(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody SoilRecordSaveRequest request) {
        Long currentUserId = requireLogin(authorization).getId();
        return Result.success(soilRecordService.create(currentUserId, request));
    }

    @PutMapping("/{id}")
    public Result<SoilRecordVO> update(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "土壤检测记录ID必须大于0") @PathVariable Long id,
            @Valid @RequestBody SoilRecordSaveRequest request) {
        Long currentUserId = requireLogin(authorization).getId();
        return Result.success(soilRecordService.update(currentUserId, id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "土壤检测记录ID必须大于0") @PathVariable Long id) {
        Long currentUserId = requireLogin(authorization).getId();
        soilRecordService.delete(currentUserId, id);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<SoilRecordVO> detail(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "土壤检测记录ID必须大于0") @PathVariable Long id) {
        Long currentUserId = requireLogin(authorization).getId();
        return Result.success(soilRecordService.detail(currentUserId, id));
    }

    @GetMapping("/page")
    public Result<PageResult<SoilRecordVO>> page(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid SoilRecordPageQuery query) {
        Long currentUserId = requireLogin(authorization).getId();
        return Result.success(soilRecordService.page(currentUserId, query));
    }

    private UserProfileVO requireLogin(String authorization) {
        return authService.getProfile(authorization);
    }
}
