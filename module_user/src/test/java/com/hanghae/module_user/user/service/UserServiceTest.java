package com.hanghae.module_user.user.service;

import com.hanghae.module_user.common.exception.CustomException;
import com.hanghae.module_user.common.exception.ErrorCode;
import com.hanghae.module_user.common.redis.service.RedisService;
import com.hanghae.module_user.user.dto.request.CreateUserRequest;
import com.hanghae.module_user.user.dto.request.UpdatePasswordRequest;
import com.hanghae.module_user.user.dto.request.UpdateUserRequest;
import com.hanghae.module_user.user.dto.response.GetUserRoleResponse;
import com.hanghae.module_user.user.dto.response.UserResponse;
import com.hanghae.module_user.user.entity.User;
import com.hanghae.module_user.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private RedisService redisService;

    @Nested
    @DisplayName("이메일 인증")
    class verificationEmail {

        @Test
        @DisplayName("이메일 인증 성공")
        void verificationEmailSuccess() {
            //Given
            String userEmail = "test@test.com";
            String requestCode = "testCode";

            when(redisService.compareValue(userEmail, requestCode)).thenReturn(true);
            //When
            boolean result = userService.isVerify(userEmail, requestCode);

            //Then
            assertTrue(result);
        }

        @Test
        @DisplayName("이메일 인증 실패")
        void verificationEmailFailEmailNotExist() {
            //Given
            String userEmail = "test2@test.com";
            String requestCode = "testCode2";

            //When
            when(redisService.compareValue(userEmail, requestCode)).thenReturn(false);

            boolean result = userService.isVerify(userEmail, requestCode);

            //Then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("회원가입")
    class SignUp {

        @Test
        @DisplayName("판매자 생성 성공")
        void createSellerSuccess() {
            // Given
            CreateUserRequest createUserRequest = new CreateUserRequest(
                    "seller@example.com",
                    "sellerPassword",
                    "sellerName",
                    User.UserRole.SELLER,
                    "sellerCode"
            );

            User user = User.create(createUserRequest, "encodedPassword");

            when(redisService.compareValue(anyString(), anyString())).thenReturn(true);
            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            UserResponse result = userService.create(createUserRequest);

            // Then
            assertNotNull(result);
            assertEquals(createUserRequest.getEmail(), result.getEmail());
            assertEquals(createUserRequest.getName(), result.getName());
        }

        @Test
        @DisplayName("유저 생성 실패 - 이메일 누락")
        void createSellerFailEmail() {
            // Given
            CreateUserRequest createUserRequest = new CreateUserRequest(
                    "",
                    "password",
                    "name",
                    User.UserRole.BUYER,
                    "code"
            );

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                userService.create(createUserRequest);
            });
            //Then
            assertEquals(ErrorCode.EMAIL_REQUIRED, exception.getErrorCode());
        }

        @Test
        @DisplayName("유저 생성 실패 - 비밀번호 누락")
        void createSellerFailPassword() {
            // Given
            CreateUserRequest createUserRequest = new CreateUserRequest(
                    "email",
                    "",
                    "name",
                    User.UserRole.BUYER,
                    "code"
            );

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                userService.create(createUserRequest);
            });
            //Then
            assertEquals(ErrorCode.PASSWORD_REQUIRED, exception.getErrorCode());
        }

        @Test
        @DisplayName("유저 생성 실패 - 이름 누락")
        void createSellerFailName() {
            // Given
            CreateUserRequest createUserRequest = new CreateUserRequest(
                    "email",
                    "password",
                    "",
                    User.UserRole.BUYER,
                    "code"
            );

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                userService.create(createUserRequest);
            });
            //Then
            assertEquals(ErrorCode.NAME_REQUIRED, exception.getErrorCode());
        }

        @Test
        @DisplayName("유저 생성 실패 - 사용자 구분 누락")
        void createSellerFailRole() {
            // Given
            CreateUserRequest createUserRequest = new CreateUserRequest(
                    "email",
                    "password",
                    "name",
                    null,
                    "code"
            );

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                userService.create(createUserRequest);
            });
            //Then
            assertEquals(ErrorCode.USER_ROLE_REQUIRED, exception.getErrorCode());
        }

        @Test
        @DisplayName("판매자 생성 실패 - 인증코드 불일치")
        void createSellerFailVerificationCode() {
            // Given
            CreateUserRequest createUserRequest = new CreateUserRequest(
                    "email",
                    "password",
                    "name",
                    User.UserRole.BUYER,
                    "code"
            );

            when(redisService.compareValue(anyString(), anyString())).thenReturn(false);

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                userService.create(createUserRequest);
            });
            //Then
            assertEquals(ErrorCode.EMAIL_AUTH_CODE_INCORRECT, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("회원 정보 조회")
    class getUserRole {

        @Test
        @DisplayName("회원정보 조회 성공")
        void getUserRoleSuccess() {
            //Given
            Long userNum = 1L;

            User expectedResult = User.builder()
                    .userNum(1L)
                    .userRole(User.UserRole.SELLER)
                    .build();

            when(userRepository.findById(userNum)).thenReturn(Optional.ofNullable(expectedResult));

            //When
            GetUserRoleResponse result = userService.getUserRole(userNum);

            //Then
            assert expectedResult != null;
            assertEquals(expectedResult.getUserNum(), result.getUserNum());
            assertEquals(expectedResult.getUserRole().toString(), result.getUserRole());
        }

        @Test
        @DisplayName("회원정보 조회 실패 - 사용자가 존재하지 않음")
        void getUserRoleWhenUserNotFound() {
            // Given
            Long userNum = 1L;

            when(userRepository.findById(userNum)).thenReturn(Optional.empty());

            // Then
            assertThrows(CustomException.class, () -> {
                // When
                userService.getUserRole(userNum);
            });
        }

        @Test
        @DisplayName("사용자 조회 실패 - 사용자 삭제됨")
        void getUserRoleWhenUserIsDeleted() {
            // Given
            Long userNum = 2L;
            User deletedUser = User.builder()
                            .build();
            deletedUser.delete();

            when(userRepository.findById(userNum)).thenReturn(Optional.of(deletedUser));

            // Then
            assertThrows(CustomException.class, () -> {
                // When
                userService.getUserRole(userNum);
            });
        }
    }

    @Nested
    @DisplayName("회원정보 수정")
    class updateUserDetails {

        @Test
        @DisplayName("회원정보 수정 성공")
        void updateUserSuccess() {
            // Given
            Long userNum = 2L;
            String newName = "afterUpdate";
            UpdateUserRequest updateUserRequest = new UpdateUserRequest(newName);

            User user = User.builder()
                    .userNum(userNum)
                    .name("beforeUpdate")
                    .build();

            when(userRepository.findById(userNum)).thenReturn(Optional.of(user));

            // When
            UserResponse userResponse = userService.update(userNum, updateUserRequest);

            // Then
            assertEquals(newName, userResponse.getName());
        }

        @Test
        @DisplayName("회원정보 수정 실패 - 사용자 존재하지 않음")
        void updateUserFailUserNotFound() {
            // Given
            Long userNum = 2L;
            UpdateUserRequest updateUserRequest = new UpdateUserRequest("newName");

            when(userRepository.findById(userNum)).thenReturn(Optional.empty());

            // Then
            assertThrows(CustomException.class, () -> {
                // When
                userService.update(userNum, updateUserRequest);
            });
        }

        @Test
        @DisplayName("회원정보 수정 실패 - 사용자 삭제됨")
        void updateUserFailUserDeleted() {
            // Given
            Long userNum = 2L;
            User deletedUser = User.builder()
                    .userNum(userNum)
                    .name("Deleted User")
                    .build();

            deletedUser.delete();

            UpdateUserRequest updateUserRequest = new UpdateUserRequest("newName");

            when(userRepository.findById(userNum)).thenReturn(Optional.of(deletedUser));

            // Then
            assertThrows(CustomException.class, () -> {
                // When
                userService.update(userNum, updateUserRequest);
            });
        }

        @Test
        @DisplayName("비밀번호 업데이트 성공")
        void updatePasswordSuccess() {
            // Given
            Long userNum = 1L;
            String originalPassword = "oldPassword";
            String newPassword = "newPassword";
            UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(originalPassword, newPassword);
            User user = User.builder()
                    .userNum(userNum)
                    .password(originalPassword)
                    .build();

            when(userRepository.findById(userNum)).thenReturn(Optional.of(user));
            when(bCryptPasswordEncoder.matches(originalPassword, user.getPassword())).thenReturn(true);

            // When
            UserResponse userResponse = userService.updatePassword(userNum, updatePasswordRequest);

            // Then
            assertNotNull(userResponse);
            assertEquals(userNum, userResponse.getUserNum());
        }

        @Test
        @DisplayName("비밀번호 업데이트 실패 - 사용자 존재하지 않음")
        void updatePasswordUserNotFound() {
            // Given
            Long userNum = 1L;
            UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest("oldPassword", "newPassword");

            when(userRepository.findById(userNum)).thenReturn(Optional.empty());

            // Then
            assertThrows(CustomException.class, () -> {
                // When
                userService.updatePassword(userNum, updatePasswordRequest);
            });
        }

        @Test
        @DisplayName("비밀번호 업데이트 실패 - 사용자 삭제됨")
        void updatePasswordUserDeleted() {
            // Given
            Long userNum = 1L;
            User deletedUser = User.builder()
                            .userNum(userNum)
                            .build();

            deletedUser.delete();

            when(userRepository.findById(userNum)).thenReturn(Optional.of(deletedUser));

            // Then
            assertThrows(CustomException.class, () -> {
                // When
                userService.updatePassword(userNum, new UpdatePasswordRequest("oldPassword", "newPassword"));
            });
        }
    }

    @Nested
    @DisplayName("회원탈퇴")
    class deleteUser {

        @Test
        @DisplayName("사용자 삭제 성공")
        void deleteUserSuccess() {
            // Given
            Long userNum = 1L;
            User user = User.builder()
                    .userNum(userNum)
                    .name("testUser")
                    .build();

            when(userRepository.findById(userNum)).thenReturn(Optional.of(user));

            // When
            UserResponse userResponse = userService.delete(userNum);

            // Then
            assertNotNull(userResponse);
            assertNotNull(user.getDeletedAt());
            assertEquals(userNum, userResponse.getUserNum());
        }

        @Test
        @DisplayName("사용자 삭제 실패 - 사용자 존재하지 않음")
        void deleteUserNotFound() {
            // Given
            Long userNum = 1L;

            when(userRepository.findById(userNum)).thenReturn(Optional.empty());

            // Then
            assertThrows(CustomException.class, () -> {
                // When
                userService.delete(userNum);
            });
        }

        @Test
        @DisplayName("사용자 삭제 실패 - 사용자 이미 삭제됨")
        void deleteUserAlreadyDeleted() {
            // Given
            Long userNum = 1L;

            User deletedUser = User.builder()
                    .userNum(userNum)
                    .build();

            deletedUser.delete();

            when(userRepository.findById(userNum)).thenReturn(Optional.of(deletedUser));

            // Then
            assertThrows(CustomException.class, () -> {
                // When
                userService.delete(userNum);
            });
        }

    }

}