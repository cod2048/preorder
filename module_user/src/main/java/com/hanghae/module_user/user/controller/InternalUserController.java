package com.hanghae.module_user.user.controller;

import com.hanghae.module_user.user.dto.response.GetUserRoleResponse;
import com.hanghae.module_user.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/users")
public class InternalUserController {
    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/check/{userNum}")
    public ResponseEntity<GetUserRoleResponse> getUserRole(@PathVariable("userNum") Long userNum) {
        GetUserRoleResponse getUserRoleResponse = userService.getUserRole(userNum);

        return ResponseEntity.ok().body(getUserRoleResponse);
    }
}
