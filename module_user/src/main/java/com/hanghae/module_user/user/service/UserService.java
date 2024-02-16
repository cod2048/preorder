package com.hanghae.module_user.user.service;

import com.hanghae.module_user.common.email.EmailService;
import com.hanghae.module_user.user.dto.request.CreateUserRequest;
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
    private final com.hanghae.module_user.redis.service.RedisService redisService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, com.hanghae.module_user.redis.service.RedisService redisService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.redisService = redisService;
    }

    @Transactional
    public User create(CreateUserRequest createUserRequest) {

        //필수요소 확인
        if (createUserRequest.getEmail() == null || createUserRequest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("email is empty");
        }
        if (createUserRequest.getName() == null || createUserRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("name is empty");
        }
        if (createUserRequest.getPassword() == null || createUserRequest.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("password is empty");
        }
        if (createUserRequest.getUserRole() == null) {
            throw new IllegalArgumentException("userRole is empty");
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

            return createdUser;
        } else {
            throw new IllegalStateException("verification code not match");
        }
    }

    public boolean isVerify(String userEmail, String requestCode) {
        return redisService.compareValue(userEmail, requestCode);
    }





}
