package com.hanghae.module_user.user.controller;

import com.hanghae.module_user.user.dto.request.CreateUserRequest;
import com.hanghae.module_user.user.entity.User;
import com.hanghae.module_user.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody CreateUserRequest createUserRequest) {
        return ResponseEntity.ok(userService.create(createUserRequest));
    }

}
