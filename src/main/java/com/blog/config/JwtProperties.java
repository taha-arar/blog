package com.blog.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private String secret;

    private long expirationMinutes = 30;

    private String cookieName = "ACCESS_TOKEN";

    private boolean cookieSecure = false;

    private String cookieSameSite = "Lax";

    private String cookiePath = "/";

}
