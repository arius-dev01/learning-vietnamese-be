package com.example.vietjapaneselearning.dto.response;

import com.example.vietjapaneselearning.dto.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartGameResponse {
    private Long id;
    private Long gameId;
    private Long playerId;
    private Long lastQuestionId;
    private LocalDateTime startTime;
    private List<QuestionDTO> questions;
    private String type;
    private Long topicId;
    private int score;
}
