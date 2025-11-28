package com.example.vietjapaneselearning.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionDTO {
    private Long gameId;
    private Long questionId;
    private String questionText;
    private String explanation;
    private boolean audio_url;
    private String image_url;
    private Long lessonId;
    private String type;
    private String answerText;
    private String questionTextJa;
    private String explanationJa;
    private List<String> sentence;
    private List<OptionDTO> options;
}
