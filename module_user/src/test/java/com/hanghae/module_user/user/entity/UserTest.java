package com.hanghae.module_user.user.entity;

import com.hanghae.module_user.user.dto.request.CreateUserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    @DisplayName("단위 테스트 : 유저 생성 성공")
    void user_create_success() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        //given
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "testUserEmail",
                "testUserPassword",
                "testUserName",
                User.UserRole.BUYER,
                "testVerificationCode"
        );

        String encodedPassword = bCryptPasswordEncoder.encode(createUserRequest.getPassword());

        //when
        User result = User.create(createUserRequest, encodedPassword);

        //then
        assertThat(result.getEmail()).isEqualTo("testUserEmail");
        assertThat(result.getName()).isEqualTo("testUserName");
    }
}