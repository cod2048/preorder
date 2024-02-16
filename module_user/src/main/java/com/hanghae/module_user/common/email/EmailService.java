package com.hanghae.module_user.common.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final com.hanghae.module_user.redis.service.RedisService redisService;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private Long timeout;

    public void sendEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        log.info(to);
        message.setTo(to);
        message.setSubject("pre-order 가입을 위한 이메일 인증 코드");
        message.setText("인증 코드: " + verificationCode);
        mailSender.send(message);
    }
    public void verifyEmail(String email) {

        //1. 가입 요청 들어오면 이메일전송(이 안에 코드 생성까지 있음)
        String verificationCode = generateRandomCode();
        log.info(verificationCode);
        sendEmail(email, verificationCode);
        //2. 레디스에 위에 쓴 이메일이랑 코드 저장(setValue)
        redisService.setValue(email, verificationCode, timeout, TimeUnit.MILLISECONDS);

    }

    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

}