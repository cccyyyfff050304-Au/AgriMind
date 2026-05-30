package com.agrimind.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "agrimind.jwt")
public class JwtProperties {

    private String secret;

    private Long expirationSeconds = 86400L;
}
