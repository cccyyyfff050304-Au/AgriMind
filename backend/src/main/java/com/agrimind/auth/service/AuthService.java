package com.agrimind.auth.service;

import com.agrimind.auth.dto.LoginRequest;
import com.agrimind.auth.dto.RegisterRequest;
import com.agrimind.auth.util.JwtTokenProvider;
import com.agrimind.auth.vo.LoginResponseVO;
import com.agrimind.common.exception.BusinessException;
import com.agrimind.user.entity.SysUser;
import com.agrimind.user.mapper.SysUserMapper;
import com.agrimind.user.vo.UserProfileVO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final String DEFAULT_ROLE_CODE = "USER";
    private static final int ENABLED_STATUS = 1;

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public UserProfileVO register(RegisterRequest request) {
        String username = normalizeUsername(request.getUsername());
        if (isUsernameExists(username)) {
            throw new BusinessException(400, "用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRealName(trimToNull(request.getRealName()));
        user.setPhone(trimToNull(request.getPhone()));
        user.setEmail(trimToNull(request.getEmail()));
        user.setRoleCode(DEFAULT_ROLE_CODE);
        user.setStatus(ENABLED_STATUS);
        user.setDeleted(0);
        sysUserMapper.insert(user);
        return UserProfileVO.from(user);
    }

    @Transactional
    public LoginResponseVO login(LoginRequest request) {
        SysUser user = getByUsername(normalizeUsername(request.getUsername()));
        if (user == null) {
            throw new BusinessException(400, "用户不存在");
        }
        if (!Integer.valueOf(ENABLED_STATUS).equals(user.getStatus())) {
            throw new BusinessException(400, "用户已禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(400, "密码错误");
        }

        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(user);

        String token = jwtTokenProvider.generateToken(user);
        return new LoginResponseVO(token, "Bearer", jwtTokenProvider.getExpirationSeconds(), UserProfileVO.from(user));
    }

    public UserProfileVO getProfile(String authorizationHeader) {
        Long userId = jwtTokenProvider.parseUserIdFromAuthorization(authorizationHeader);
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "Token 无效或已过期");
        }
        if (!Integer.valueOf(ENABLED_STATUS).equals(user.getStatus())) {
            throw new BusinessException(401, "用户已禁用");
        }
        return UserProfileVO.from(user);
    }

    private boolean isUsernameExists(String username) {
        Long count = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, username));
        return count != null && count > 0;
    }

    private SysUser getByUsername(String username) {
        return sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, username));
    }

    private String normalizeUsername(String username) {
        return username.trim();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
