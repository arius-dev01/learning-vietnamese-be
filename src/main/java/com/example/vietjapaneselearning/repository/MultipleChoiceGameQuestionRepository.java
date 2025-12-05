package com.example.vietjapaneselearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.vietjapaneselearning.model.MultipleChoiceQuestion;

public interface MultipleChoiceGameQuestionRepository extends JpaRepository<MultipleChoiceQuestion, Long> {
    long countByGameIdAndLessonId(Long gameId, Long lessonId);

    // List<MultipleChoiceQuestion> findByTopic(Topic topic);
    // List<MultipleChoiceQuestion> findByGameIdAndLessonId(Long gameId, Long
    // lessonId);
    @Query("SELECT m FROM MultipleChoiceQuestion m where m.lesson.id = :lessonId AND m.game.id = :game_id")
    List<MultipleChoiceQuestion> findByLessonIdAndGameId(@Param("lessonId") Long lessonId,
            @Param("game_id") Long game_id);

    long countByGameId(Long game_id);
}
