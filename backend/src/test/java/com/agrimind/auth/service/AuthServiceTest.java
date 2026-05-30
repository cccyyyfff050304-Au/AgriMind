package com.agrimind.auth.service;

import com.agrimind.auth.dto.LoginRequest;
import com.agrimind.auth.dto.RegisterRequest;
import com.agrimind.auth.util.JwtTokenProvider;
import com.agrimind.auth.vo.LoginResponseVO;
import com.agrimind.common.exception.BusinessException;
import com.agrimind.user.entity.SysUser;
import com.agrimind.user.mapper.SysUserMapper;
import com.agrimind.user.vo.UserProfileVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(sysUserMapper, passwordEncoder, jwtTokenProvider);
    }

    @Test
    void registerShouldEncryptPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("secret123");
        when(sysUserMapper.selectCount(anyUserWrapper())).thenReturn(0L);
        when(sysUserMapper.insert(any(SysUser.class))).thenAnswer(invocation -> {
            SysUser user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        });

        UserProfileVO profile = authService.register(request);

        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(sysUserMapper).insert(userCaptor.capture());
        SysUser savedUser = userCaptor.getValue();
        assertThat(profile.getUsername()).isEqualTo("test_user");
        assertThat(savedUser.getPasswordHash()).isNotEqualTo("secret123");
        assertThat(passwordEncoder.matches("secret123", savedUser.getPasswordHash())).isTrue();
    }

    @Test
    void registerShouldRejectDuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("secret123");
        when(sysUserMapper.selectCount(anyUserWrapper())).thenReturn(1L);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("用户名已存在");
    }

    @Test
    void loginShouldReturnTokenWhenPasswordMatches() {
        LoginRequest request = new LoginRequest();
        request.setUsername("test_user");
        request.setPassword("secret123");
        SysUser user = user("test_user", passwordEncoder.encode("secret123"));
        when(sysUserMapper.selectOne(anyUserWrapper())).thenReturn(user);
        when(jwtTokenProvider.generateToken(user)).thenReturn("jwt-token");
        when(jwtTokenProvider.getExpirationSeconds()).thenReturn(86400L);

        LoginResponseVO response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getUser().getUsername()).isEqualTo("test_user");
        verify(sysUserMapper).updateById(user);
    }

    @Test
    void loginShouldRejectWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("test_user");
        request.setPassword("wrong-password");
        when(sysUserMapper.selectOne(anyUserWrapper())).thenReturn(user("test_user", passwordEncoder.encode("secret123")));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("密码错误");
    }

    private SysUser user(String username, String passwordHash) {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setRoleCode("USER");
        user.setStatus(1);
        user.setDeleted(0);
        return user;
    }

    @SuppressWarnings("unchecked")
    private Wrapper<SysUser> anyUserWrapper() {
        return any(Wrapper.class);
    }
}
