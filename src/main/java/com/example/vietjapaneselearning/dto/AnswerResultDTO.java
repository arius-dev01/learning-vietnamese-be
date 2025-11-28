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
public class AnswerResultDTO {
    private boolean correct;
    private Long correctOptionId;
    private String explanation;
    private List<Option> options;
    private boolean complete = false;
    private Integer totalScore;
    private Integer currentStreak;
    private Integer wrongCount;
    private int newTotalScore;
    private int bonus;

}
