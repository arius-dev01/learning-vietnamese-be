package com.example.vietjapaneselearning.dto;

import com.example.vietjapaneselearning.model.Option;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerDTO {
    private Long gameId;
    private Long questionId;
    private Long playerId;
    private Long lessonId;
    private List<String> answer;
    private Long optionId;
}
