package com.hanghae.module_user.user.entity;

import com.hanghae.module_user.user.dto.request.CreateUserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class UserTest {

    @Test
    @DisplayName("사용자 생성")
    void createUser() {
        // Given
        CreateUserRequest request = new CreateUserRequest("test@example.com", "rawPassword", "Test User", User.UserRole.BUYER, "verificationCode");
        String encodedPassword = "encodedPassword";

        // When
        User user = User.create(request, encodedPassword);

        // Then
        assertNotNull(user);
        assertEquals(request.getEmail(), user.getEmail());
        assertEquals(encodedPassword, user.getPassword());
        assertEquals(request.getName(), user.getName());
        assertEquals(request.getUserRole(), user.getUserRole());
        assertNull(user.getDeletedAt());
    }

    @Test
    @DisplayName("사용자 정보 수정")
    void updateUserInformation() {
        // Given
        User user = User.builder()
                .email("updateTest@example.com")
                .password("password")
                .name("Original Name")
                .userRole(User.UserRole.SELLER)
                .build();

        // When
        String updatedName = "Updated Name";
        user.update(updatedName);

        // Then
        assertEquals(updatedName, user.getName());
    }

    @Test
    @DisplayName("비밀번호 수정")
    void updatePassword() {
        // Given
        User user = User.builder()
                .email("passwordUpdateTest@example.com")
                .password("oldPassword")
                .name("User")
                .userRole(User.UserRole.BUYER)
                .build();

        // When
        String newPassword = "newPassword";
        user.updatePassword(newPassword);

        // Then
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    @DisplayName("사용자 삭제")
    void deleteUser() {
        // Given
        User user = User.builder()
                .email("deleteTest@example.com")
                .password("password")
                .name("Delete Test")
                .userRole(User.UserRole.BUYER)
                .build();

        // When
        user.delete();

        // Then
        assertNotNull(user.getDeletedAt());
        assertTrue(user.getDeletedAt().isBefore(LocalDateTime.now().plusSeconds(1))); // 삭제 시간이 현재 시간 이전이어야 함
    }
}
