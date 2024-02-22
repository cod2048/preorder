package com.hanghae.module_user.user.entity;

import com.hanghae.module_user.user.dto.request.CreateUserRequest;
import com.hanghae.module_user.user.dto.request.UpdateUserRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_num")
    private Long userNum;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "role", nullable = false)
    private UserRole userRole;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public User(Long userNum, String email, String password, String name, UserRole userRole) {
        this.userNum = userNum;
        this.email = email;
        this.password = password;
        this.name = name;
        this.userRole = userRole;
    }

    public static User create(CreateUserRequest createUserRequest, String encodedPassword) {

        return User.builder()
                .email(createUserRequest.getEmail())
                .password(encodedPassword)
                .name(createUserRequest.getName())
                .userRole(createUserRequest.getUserRole())
                .build();
    }

    public void update(String newName) {
        this.name = newName;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public enum UserRole {
        SELLER,
        BUYER
    }

}


