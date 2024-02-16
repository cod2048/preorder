package com.hanghae.module_user.user.dto.request;

import com.hanghae.module_user.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CreateUserRequest {
    private String email;
    private String password;
    private String name;
    private User.UserRole userRole;
    private String verificationCode;
}
