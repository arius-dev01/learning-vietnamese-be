package com.example.vietjapaneselearning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameDTO {
    private Long id;
    private String title;
    private Long gameTypeId;
    private String description;
    private String titleLesson;
    private String type;
    private Long lessonId;
    private List<QuestionDTO> questions;
    private Long gameCount;
}
