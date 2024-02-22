package com.hanghae.module_user.user.service;

import com.hanghae.module_user.common.email.EmailService;
import com.hanghae.module_user.common.exception.CustomException;
import com.hanghae.module_user.common.exception.ErrorCode;
import com.hanghae.module_user.user.dto.request.CreateUserRequest;
import com.hanghae.module_user.user.dto.request.UpdatePasswordRequest;
import com.hanghae.module_user.user.dto.request.UpdateUserRequest;
import com.hanghae.module_user.user.dto.response.GetUserRoleResponse;
import com.hanghae.module_user.user.dto.response.UserResponse;
import com.hanghae.module_user.user.entity.User;
import com.hanghae.module_user.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final com.hanghae.module_user.common.redis.service.RedisService redisService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, com.hanghae.module_user.common.redis.service.RedisService redisService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.redisService = redisService;
    }

    @Transactional
    public UserResponse create(CreateUserRequest createUserRequest) {

        //필수요소 확인
        if (createUserRequest.getEmail() == null || createUserRequest.getEmail().trim().isEmpty()) {
            throw new CustomException(ErrorCode.EMAIL_REQUIRED);
        }
        if (createUserRequest.getName() == null || createUserRequest.getName().trim().isEmpty()) {
            throw new CustomException(ErrorCode.NAME_REQUIRED);
        }
        if (createUserRequest.getPassword() == null || createUserRequest.getPassword().trim().isEmpty()) {
            throw new CustomException(ErrorCode.PASSWORD_REQUIRED);
        }
        if (createUserRequest.getUserRole() == null) {
            throw new CustomException(ErrorCode.USER_ROLE_REQUIRED);
        }

        String verificationCode = createUserRequest.getVerificationCode();
        String userEmail = createUserRequest.getEmail();

        if (isVerify(userEmail, verificationCode)) {
            String encodedPassword = bCryptPasswordEncoder.encode(createUserRequest.getPassword());
            //1. userRequest로 entity 생성
            User newUser = User.create(createUserRequest, encodedPassword);

            //2. 생성된 entity db에 저장
            User createdUser = userRepository.save(newUser);

            redisService.deleteValue(userEmail);

            return new UserResponse(createdUser.getUserNum(), createdUser.getName(), createdUser.getEmail());
        } else {
            throw new CustomException(ErrorCode.EMAIL_AUTH_CODE_INCORRECT);
        }
    }

    public boolean isVerify(String userEmail, String requestCode) {
        return redisService.compareValue(userEmail, requestCode);
    }

    public GetUserRoleResponse getUserRole(Long userNum) {
        User findUser = userRepository.findById(userNum)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (findUser.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        return new GetUserRoleResponse(findUser.getUserNum(), findUser.getUserRole().toString());
    }

    @Transactional
    public UserResponse update(Long userNum, UpdateUserRequest updateUserRequest) {
        User targetUser = userRepository.findById(userNum)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (targetUser.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        targetUser.update(updateUserRequest.getName());

        return new UserResponse(targetUser.getUserNum(), targetUser.getName(), targetUser.getEmail());
    }

    @Transactional
    public UserResponse delete(Long userNum) {
        User targetUser = userRepository.findById(userNum)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        targetUser.delete();

        return new UserResponse(targetUser.getUserNum(), targetUser.getName(), targetUser.getEmail());
    }

    @Transactional
    public UserResponse updatePassword(Long userNum, UpdatePasswordRequest updatePasswordRequest) {
        User targetUser = userRepository.findById(userNum)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (targetUser.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        String encodedNewPassword = bCryptPasswordEncoder.encode(updatePasswordRequest.getPassword());

        targetUser.updatePassword(encodedNewPassword);

        return new UserResponse(targetUser.getUserNum(), targetUser.getName(), targetUser.getEmail());
    }
}
