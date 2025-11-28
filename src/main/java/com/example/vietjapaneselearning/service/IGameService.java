package com.example.vietjapaneselearning.service;

import com.example.vietjapaneselearning.dto.*;
import com.example.vietjapaneselearning.dto.response.StartGameResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGameService {
    List<QuestionDTO> addQuestion(List<QuestionDTO> dto, Long topicId);

    AnswerResultDTO submitAnswer(AnswerDTO answerDTO);

    StartGameResponse startGame(String typeGame, Long lessonId);

    Page<GameDTO> findGameByLessonId(Long lessonId, Pageable pageable);

    List<QuestionDTO> findQuestionByLessonIdAndGameId(Long lessonId, Long gameId);
    List<RecentActivityDTO> getRecentActivities();
    void deleteGame(Long gameId);
//    List<GameDTO> findAll();
}
