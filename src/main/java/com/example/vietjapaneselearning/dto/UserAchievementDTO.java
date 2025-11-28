package com.example.vietjapaneselearning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAchievementDTO {
    private Long id;
    private Long totalScore;
    private Long totalLesson;
    private Long totalGame;
    private Long learningSteak;
}
