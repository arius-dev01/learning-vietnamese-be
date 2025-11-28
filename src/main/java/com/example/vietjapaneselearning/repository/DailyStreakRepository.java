package com.example.vietjapaneselearning.repository;

import com.example.vietjapaneselearning.model.DailyStreak;
import com.example.vietjapaneselearning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyStreakRepository extends JpaRepository<DailyStreak,Long> {
    DailyStreak findByUser(User user);
}
