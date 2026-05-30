package com.agrimind.auth.util;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.config.JwtProperties;
import com.agrimind.user.entity.SysUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("12345678901234567890123456789012");
        properties.setExpirationSeconds(3600L);
        jwtTokenProvider = new JwtTokenProvider(properties);
    }

    @Test
    void shouldGenerateAndParseToken() {
        SysUser user = new SysUser();
        user.setId(7L);
        user.setUsername("test_user");
        user.setRoleCode("USER");

        String token = jwtTokenProvider.generateToken(user);
        Long userId = jwtTokenProvider.parseUserIdFromAuthorization("Bearer " + token);

        assertThat(userId).isEqualTo(7L);
    }

    @Test
    void shouldRejectMissingToken() {
        assertThatThrownBy(() -> jwtTokenProvider.parseUserIdFromAuthorization(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Token 缺失");
    }

    @Test
    void shouldRejectInvalidToken() {
        assertThatThrownBy(() -> jwtTokenProvider.parseUserIdFromAuthorization("Bearer invalid-token"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Token 无效或已过期");
    }
}
