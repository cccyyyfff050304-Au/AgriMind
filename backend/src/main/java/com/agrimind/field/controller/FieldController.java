package com.agrimind.field.controller;

import com.agrimind.auth.service.AuthService;
import com.agrimind.common.result.PageResult;
import com.agrimind.common.result.Result;
import com.agrimind.field.dto.FieldPageQuery;
import com.agrimind.field.dto.FieldSaveRequest;
import com.agrimind.field.service.FieldService;
import com.agrimind.field.vo.FieldOptionVO;
import com.agrimind.field.vo.FieldVO;
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

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/fields")
public class FieldController {

    private final FieldService fieldService;
    private final AuthService authService;

    public FieldController(FieldService fieldService, AuthService authService) {
        this.fieldService = fieldService;
        this.authService = authService;
    }

    @PostMapping
    public Result<FieldVO> create(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody FieldSaveRequest request) {
        return Result.success(fieldService.create(currentUserId(authorization), request));
    }

    @PutMapping("/{id}")
    public Result<FieldVO> update(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "地块ID必须大于0") @PathVariable Long id,
            @Valid @RequestBody FieldSaveRequest request) {
        return Result.success(fieldService.update(currentUserId(authorization), id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "地块ID必须大于0") @PathVariable Long id) {
        fieldService.delete(currentUserId(authorization), id);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<FieldVO> detail(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "地块ID必须大于0") @PathVariable Long id) {
        return Result.success(fieldService.detail(currentUserId(authorization), id));
    }

    @GetMapping("/page")
    public Result<PageResult<FieldVO>> page(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid FieldPageQuery query) {
        return Result.success(fieldService.page(currentUserId(authorization), query));
    }

    @GetMapping("/list")
    public Result<List<FieldOptionVO>> list(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return Result.success(fieldService.list(currentUserId(authorization)));
    }

    private Long currentUserId(String authorization) {
        UserProfileVO profile = authService.getProfile(authorization);
        return profile.getId();
    }
}
