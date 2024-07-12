package com.rabbani.chatapp.v1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public interface AuthControllerDto {

    @Data
    class SignInResponse{
        private String token;
        private String refreshToken;
        private Long expiresIn;
    }

    @Data
    class RefreshSessionRequest{
        private String refreshToken;
    }

    @Data
    class RefreshSessionResponse{
        private String token;
        private String refreshToken;
        private Long expiresIn;
    }

}
