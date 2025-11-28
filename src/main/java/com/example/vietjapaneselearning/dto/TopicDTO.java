package com.example.vietjapaneselearning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicDTO {
    private Long id;
    private String name;
    private Long gameId;
    private Long score;
    private Long questionId;
    private LocalDateTime createdAt;
    private String description;
    private boolean completed;
    private String typeGame;
}
