package com.example.vietjapaneselearning.task;

import com.example.vietjapaneselearning.repository.PasswordResetTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TokenCleanupTask {
    @Autowired
    private  PasswordResetTokenRepository passwordResetTokenRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanExpiredTokens(){
        passwordResetTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
