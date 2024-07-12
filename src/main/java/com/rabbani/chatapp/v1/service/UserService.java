package com.rabbani.chatapp.v1.service;

import com.rabbani.chatapp.v1.dto.Response;
import com.rabbani.chatapp.v1.dto.UserControllerDto;

public interface UserService {

    Response<UserControllerDto.GetMeResponse> getMe();
}
