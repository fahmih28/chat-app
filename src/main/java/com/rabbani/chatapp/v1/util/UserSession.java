package com.rabbani.chatapp.v1.util;

import lombok.Data;

public interface UserSession {

    String getId();

    String getFirstName();

    String getLastName();

    String getEmail();

    Long[] getQualifier();

    String getToken();

    String getRefreshToken();

    long getExpiresAt();

    @Data
    class RedisUserSession implements UserSession {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private Long[] qualifier;
        private String token;
        private String refreshToken;
        private long expiresAt;
    }
}
