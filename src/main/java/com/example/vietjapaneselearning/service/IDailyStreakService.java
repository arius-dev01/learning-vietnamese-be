package com.example.vietjapaneselearning.service;

import com.example.vietjapaneselearning.dto.response.DailyStreakResponse;

public interface IDailyStreakService {
    void handleCheckIn();
    DailyStreakResponse getTotalCheckInDays();
}
