package com.hanghae.module_user.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public void setValue(String to, String verificationCode, Long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(to, verificationCode, timeout, timeUnit);
    }

    public boolean compareValue(String email, String verificationCode) {
        if (emailNotMatch(email)){
            throw new IllegalStateException("email not exist");
        }

        String findCode = redisTemplate.opsForValue().get(email);
        return findCode != null && findCode.equals(verificationCode);
    }

    public boolean emailNotMatch(String email){
        return Boolean.FALSE.equals(redisTemplate.hasKey(email));
    }

    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

}
