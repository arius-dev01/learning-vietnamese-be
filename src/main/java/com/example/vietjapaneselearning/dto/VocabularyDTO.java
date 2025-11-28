package com.example.vietjapaneselearning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VocabularyDTO {
    private Long id;
    private String word;
    private String meaning;
    private String pronunciation;
    private String lesson;
}
