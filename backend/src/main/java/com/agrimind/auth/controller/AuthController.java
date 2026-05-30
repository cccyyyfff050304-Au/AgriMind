package com.agrimind.auth.controller;

import com.agrimind.auth.dto.LoginRequest;
import com.agrimind.auth.dto.RegisterRequest;
import com.agrimind.auth.service.AuthService;
import com.agrimind.auth.vo.LoginResponseVO;
import com.agrimind.common.result.Result;
import com.agrimind.user.vo.UserProfileVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<UserProfileVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }
}
