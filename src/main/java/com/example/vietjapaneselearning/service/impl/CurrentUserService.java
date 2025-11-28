package com.example.vietjapaneselearning.service.impl;

import com.example.vietjapaneselearning.model.User;
import com.example.vietjapaneselearning.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    @Autowired
    private UserRepository userRepository;

    public User getUserCurrent() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new EntityNotFoundException("No authenticated user found");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Not found user with email: " + email));
    }
}
