package com.rabbani.chatapp.v1.dto;

import lombok.Data;

public interface UserControllerDto {

    @Data
    class GetMeResponse{
        private String id;
        private String icon;
        private String firstName;
        private String lastName;
    }

}
