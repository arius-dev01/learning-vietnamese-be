package com.example.vietjapaneselearning.controller;

import com.example.vietjapaneselearning.dto.response.DailyStreakResponse;
import com.example.vietjapaneselearning.service.IDailyStreakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/daily-streak")
public class DailyStreakController {
    @Autowired
    private IDailyStreakService dailyStreakService;
    @PostMapping("/check-in")
    public ResponseEntity<Void> checkIn() {
        dailyStreakService.handleCheckIn();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/total")
    public ResponseEntity<DailyStreakResponse> getTotalCheckInDays() {
        DailyStreakResponse total = dailyStreakService.getTotalCheckInDays();
        return ResponseEntity.ok(total);
    }

}
