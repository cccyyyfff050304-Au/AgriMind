package com.agrimind.crop.controller;

import com.agrimind.auth.service.AuthService;
import com.agrimind.common.result.PageResult;
import com.agrimind.common.result.Result;
import com.agrimind.crop.dto.CropPageQuery;
import com.agrimind.crop.dto.CropSaveRequest;
import com.agrimind.crop.service.CropService;
import com.agrimind.crop.vo.CropOptionVO;
import com.agrimind.crop.vo.CropVO;
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
@RequestMapping("/api/crops")
public class CropController {

    private final CropService cropService;
    private final AuthService authService;

    public CropController(CropService cropService, AuthService authService) {
        this.cropService = cropService;
        this.authService = authService;
    }

    @PostMapping
    public Result<CropVO> create(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody CropSaveRequest request) {
        requireLogin(authorization);
        return Result.success(cropService.create(request));
    }

    @PutMapping("/{id}")
    public Result<CropVO> update(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "作物ID必须大于0") @PathVariable Long id,
            @Valid @RequestBody CropSaveRequest request) {
        requireLogin(authorization);
        return Result.success(cropService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "作物ID必须大于0") @PathVariable Long id) {
        requireLogin(authorization);
        cropService.delete(id);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<CropVO> detail(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "作物ID必须大于0") @PathVariable Long id) {
        requireLogin(authorization);
        return Result.success(cropService.detail(id));
    }

    @GetMapping("/page")
    public Result<PageResult<CropVO>> page(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid CropPageQuery query) {
        requireLogin(authorization);
        return Result.success(cropService.page(query));
    }

    @GetMapping("/list")
    public Result<List<CropOptionVO>> list(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        requireLogin(authorization);
        return Result.success(cropService.list());
    }

    private void requireLogin(String authorization) {
        authService.getProfile(authorization);
    }
}
