package com.agrimind.indicator.controller;

import com.agrimind.auth.service.AuthService;
import com.agrimind.common.result.Result;
import com.agrimind.indicator.service.SoilEvaluationService;
import com.agrimind.indicator.vo.SoilEvaluationVO;
import com.agrimind.user.vo.UserProfileVO;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/soil-records")
public class SoilEvaluationController {

    private final SoilEvaluationService evaluationService;
    private final AuthService authService;

    public SoilEvaluationController(SoilEvaluationService evaluationService, AuthService authService) {
        this.evaluationService = evaluationService;
        this.authService = authService;
    }

    @PostMapping("/{id}/evaluate")
    public Result<SoilEvaluationVO> evaluate(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "土壤检测记录ID必须大于0") @PathVariable Long id) {
        Long currentUserId = requireLogin(authorization).getId();
        return Result.success(evaluationService.evaluate(currentUserId, id));
    }

    @GetMapping("/{id}/evaluation")
    public Result<SoilEvaluationVO> evaluation(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Min(value = 1, message = "土壤检测记录ID必须大于0") @PathVariable Long id) {
        Long currentUserId = requireLogin(authorization).getId();
        return Result.success(evaluationService.getEvaluation(currentUserId, id));
    }

    private UserProfileVO requireLogin(String authorization) {
        return authService.getProfile(authorization);
    }
}
