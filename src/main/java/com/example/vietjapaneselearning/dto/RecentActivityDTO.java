package com.example.vietjapaneselearning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentActivityDTO {
    private String fullName;    // Tên người chơi
    private String action;
    private String typeGame;// Hành động: "Completed game" / "Completed lesson"
    private String title;       // Tên game hoặc lesson
    private long minutesAgo;
    private String avatar;
}
