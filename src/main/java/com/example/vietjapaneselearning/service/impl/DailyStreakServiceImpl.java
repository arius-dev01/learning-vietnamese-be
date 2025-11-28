package com.example.vietjapaneselearning.service.impl;

import com.example.vietjapaneselearning.dto.response.DailyStreakResponse;
import com.example.vietjapaneselearning.model.DailyStreak;
import com.example.vietjapaneselearning.repository.DailyStreakRepository;
import com.example.vietjapaneselearning.service.IDailyStreakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DailyStreakServiceImpl implements IDailyStreakService {
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private DailyStreakRepository dailyStreakRepository;

    @Override
    public void handleCheckIn() {
        DailyStreak dailyStreak = dailyStreakRepository.findByUser(currentUserService.getUserCurrent());
        if (dailyStreak == null) {
            DailyStreak newDailyStreak = DailyStreak
                    .builder()
                    .user(currentUserService.getUserCurrent())
                    .streakCount(1)
                    .lastPlayedDate(LocalDate.now())
                    .build();
            dailyStreakRepository.save(newDailyStreak);
        } else {
            LocalDate now = LocalDate.now();
            LocalDate lastPlayedDate = dailyStreak.getLastPlayedDate();
            if (!now.equals(lastPlayedDate)) {
                if (lastPlayedDate.plusDays(1).equals(now)) {
                    dailyStreak.setStreakCount(dailyStreak.getStreakCount() + 1);
                } else {
                    dailyStreak.setStreakCount(1);
                }
                dailyStreak.setLastPlayedDate(now);

            }
            dailyStreakRepository.save(dailyStreak);
        }
    }

    @Override
    public DailyStreakResponse getTotalCheckInDays() {
        DailyStreak dailyStreak = dailyStreakRepository.findByUser(currentUserService.getUserCurrent());
        DailyStreakResponse dailyStreakResponse = new DailyStreakResponse();
        if (dailyStreak != null) {
            LocalDate now = LocalDate.now();
            LocalDate lastPlayedDate = dailyStreak.getLastPlayedDate();
            dailyStreakResponse.setTotalCheckInDays(dailyStreak.getStreakCount());
            if (now.equals(lastPlayedDate)) {
                dailyStreakResponse.setTotalCheckInDays(dailyStreak.getStreakCount());
                dailyStreakResponse.setCheckIn(true);
            }
        }
        return dailyStreakResponse;
    }

}
