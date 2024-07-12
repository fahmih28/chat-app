package com.rabbani.chatapp.v1.controller;

import com.rabbani.chatapp.v1.dto.Response;
import com.rabbani.chatapp.v1.dto.UserControllerDto;
import com.rabbani.chatapp.v1.service.UserService;
import com.rabbani.chatapp.v1.util.Session;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/v1/user")
public class UserController {

    private final UserService userService;

    private final Session session;

    private Session.Validator meValidator;

    public UserController(UserService userService, Session session) {
        this.userService = userService;
        this.session = session;
    }

    @GetMapping("/me")
    public ResponseEntity<Response<UserControllerDto.GetMeResponse>> getMe(){
        if(meValidator == null){
            meValidator = session.auth();
        }
        meValidator.validate();

        return ResponseEntity.ok(userService.getMe());

    }
}
