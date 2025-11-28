package com.example.vietjapaneselearning.controller;

import com.example.vietjapaneselearning.dto.UserAchievementDTO;
import com.example.vietjapaneselearning.service.IUserAchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-achievement")
public class UserAchievementController {
    @Autowired
    private IUserAchievementService userAchievementService;

    @GetMapping("")
    public ResponseEntity<UserAchievementDTO> getUserAchievement() {
        return ResponseEntity.ok(userAchievementService.getUserAchievement());
    }
}
