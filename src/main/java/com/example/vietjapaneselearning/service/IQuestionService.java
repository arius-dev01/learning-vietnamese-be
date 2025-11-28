package com.example.vietjapaneselearning.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.vietjapaneselearning.dto.QuestionDTO;

public interface IQuestionService {
    List<QuestionDTO> addQuestion(String typeGame, Long lessonId, List<QuestionDTO> questionDTO);

    List<QuestionDTO> updateQuestion(Long gameId, Long lessonId, List<QuestionDTO> questions);

    void deleteQuestion(Long questionId, Long gameId);

    List<QuestionDTO> importExcelMultipleChoice(MultipartFile file);

    List<QuestionDTO> importExcelListenChoice(MultipartFile file);

    List<QuestionDTO> importExcelArrange(MultipartFile file);
}
