package com.hanghae.module_user.user.controller;

import com.hanghae.module_user.common.dto.response.ApiResponse;
import com.hanghae.module_user.user.dto.request.CreateUserRequest;
import com.hanghae.module_user.user.dto.request.UpdatePasswordRequest;
import com.hanghae.module_user.user.dto.request.UpdateUserRequest;
import com.hanghae.module_user.user.dto.response.UserResponse;
import com.hanghae.module_user.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(@RequestBody CreateUserRequest createUserRequest) {
        UserResponse userResponse = userService.create(createUserRequest);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "유저 생성 성공",
                userResponse
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{userNum}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable Long userNum,
                                                            @RequestBody UpdateUserRequest updateUserRequest
    ) {
        UserResponse userResponse = userService.update(userNum, updateUserRequest);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "유저 정보 업데이트 성공",
                userResponse
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{userNum}")
    public ResponseEntity<ApiResponse<UserResponse>> delete(@PathVariable Long userNum) {
        UserResponse userResponse = userService.delete(userNum);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "삭제된 유저 : ",
                userResponse
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/password/{userNum}")
    public ResponseEntity<ApiResponse<UserResponse>> updatePassword(@PathVariable Long userNum,
                                                                    @RequestBody UpdatePasswordRequest updatePasswordRequest
    ) {
        UserResponse userResponse = userService.updatePassword(userNum, updatePasswordRequest);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "비밀번호 업데이트 성공",
                userResponse
        );
        return ResponseEntity.ok(response);
    }

}
