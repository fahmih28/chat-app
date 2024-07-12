package com.rabbani.chatapp.v1.service.impl;

import com.rabbani.chatapp.v1.dto.Response;
import com.rabbani.chatapp.v1.dto.UserControllerDto;
import com.rabbani.chatapp.v1.repository.UserRepository;
import com.rabbani.chatapp.v1.service.UserService;
import com.rabbani.chatapp.v1.util.Session;
import com.rabbani.chatapp.v1.util.UserSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    private final Session  session;

    @Override
    public Response<UserControllerDto.GetMeResponse> getMe() {
        UserSession userSession = session.getSession();
        UserControllerDto.GetMeResponse response = new UserControllerDto.GetMeResponse();
        response.setId(userSession.getId());
        response.setFirstName(response.getFirstName());
        response.setLastName(response.getLastName());
        return new Response<>(response);
    }


}
