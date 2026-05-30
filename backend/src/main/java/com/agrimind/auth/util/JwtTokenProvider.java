package com.agrimind.auth.util;

import com.agrimind.common.exception.BusinessException;
import com.agrimind.config.JwtProperties;
import com.agrimind.user.entity.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProperties jwtProperties;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(SysUser user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(getExpirationSeconds());

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("roleCode", user.getRoleCode())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey())
                .compact();
    }

    public Long parseUserIdFromAuthorization(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(401, "Token 缺失");
        }
        return parseUserId(authorizationHeader.substring(BEARER_PREFIX.length()));
    }

    public Long getExpirationSeconds() {
        Long expirationSeconds = jwtProperties.getExpirationSeconds();
        if (expirationSeconds == null || expirationSeconds <= 0) {
            throw new BusinessException(500, "JWT 过期时间配置不正确");
        }
        return expirationSeconds;
    }

    private Long parseUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.valueOf(claims.getSubject());
        } catch (JwtException | IllegalArgumentException exception) {
            throw new BusinessException(401, "Token 无效或已过期");
        }
    }

    private SecretKey signingKey() {
        String secret = jwtProperties.getSecret();
        if (!StringUtils.hasText(secret)) {
            throw new BusinessException(500, "JWT 密钥未配置");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new BusinessException(500, "JWT 密钥长度不能少于32个字符");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
