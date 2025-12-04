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
public class LessonDTO {
    private Long id;
    private String title;
    private int status;
    private long gameCount;
    private String describe;
    private String level;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String video_url;
    private String content;
    private LocalDateTime time;
    private String describeJa;
    private String titleJa;
    private String contentJa;
    private List<VocabularyDTO> vocabularies;
    private Long progress;
    private Long countCompleted;
    private List<String> typeGame;
    private Long totalUser;

}
