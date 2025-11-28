package com.example.vietjapaneselearning.service;

import jakarta.mail.MessagingException;

public interface IPasswordResetService {
    void forgotPassword(String email) throws MessagingException;
    void resetPassword(String token, String newPassword);
}
