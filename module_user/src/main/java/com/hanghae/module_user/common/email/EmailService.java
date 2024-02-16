package com.hanghae.module_user.common.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        log.info(to);
        message.setTo(to);
        message.setSubject("pre-order 가입을 위한 이메일 인증 코드");
        message.setText("인증 코드: " + verificationCode);
        mailSender.send(message);

    }

}