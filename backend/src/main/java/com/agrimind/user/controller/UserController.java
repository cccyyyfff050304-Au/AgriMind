package com.agrimind.user.controller;

import com.agrimind.auth.service.AuthService;
import com.agrimind.common.result.Result;
import com.agrimind.user.vo.UserProfileVO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/profile")
    public Result<UserProfileVO> profile(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return Result.success(authService.getProfile(authorization));
    }
}
