package com.example.vietjapaneselearning.dto.response;

import lombok.Data;

@Data
public class DailyStreakResponse {
    private long totalCheckInDays;
    private boolean checkIn;
}
