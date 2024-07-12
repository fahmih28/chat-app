package com.rabbani.chatapp.v1.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private Session session;

    @Data
    public static class Session{

        private Duration tokenTtl;
        private Duration refreshTokenTtl;
    }
}
