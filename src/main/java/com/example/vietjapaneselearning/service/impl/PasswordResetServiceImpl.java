package com.example.vietjapaneselearning.service.impl;

import com.example.vietjapaneselearning.model.PasswordResetToken;
import com.example.vietjapaneselearning.model.User;
import com.example.vietjapaneselearning.repository.PasswordResetTokenRepository;
import com.example.vietjapaneselearning.repository.UserRepository;
import com.example.vietjapaneselearning.service.IPasswordResetService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements IPasswordResetService {
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void forgotPassword(String email) throws MessagingException {
        Optional<User> optionalUser  = Optional.ofNullable(userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Not found user with email: " + email)));
        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken
                .builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();
        passwordResetTokenRepository.save(passwordResetToken);
        String resetUrl = "http://localhost:3000/reset-password?token=" + token;

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        String htmlMsg = "<!DOCTYPE html>"
                + "<html><body style='font-family:Arial,sans-serif;'>"
                + "<h2>Password Reset Request</h2>"
                + "<p>Hello,</p>"
                + "<p>We received a request to reset your password.</p>"
                + "<p>Click the button below to reset your password:</p>"
                + "<a href='" + resetUrl + "' style='display:inline-block;padding:10px 20px;"
                + "font-size:16px;color:white;background-color:#0d9488;text-decoration:none;"
                + "border-radius:5px;'>Reset Password</a>"
                + "<p>If you did not request a password reset, please ignore this email.</p>"
                + "<p>Thanks,<br/>Your Company Team</p>"
                + "</body></html>";

        helper.setTo(user.getEmail());
        helper.setSubject("Password Reset Request");
        helper.setText(htmlMsg, true);
        helper.setFrom("noreply@yourcompany.com");
        javaMailSender.send(message);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if(passwordResetToken.isEmpty()){
            throw new EntityNotFoundException("Not found account with token : " + token);
        }
        if (passwordResetToken.get().isExpired() || passwordResetToken.get().isUsed()) {
            throw new IllegalArgumentException("Token expired or already used");
        }
        User user = passwordResetToken.get().getUser();
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        passwordResetToken.get().setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken.get());
    }
}