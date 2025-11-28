package com.example.vietjapaneselearning.repository;

import com.example.vietjapaneselearning.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {
    @Query("SELECT COUNT(o) > 0 FROM Option o WHERE o.question.id = :questionId AND o.id = :optionId AND o.isCorrect = true")
    Optional<Option> existsCorrectOption(@Param("questionId") Long questionId, @Param("optionId") Long optionId);

    @Query("SELECT o FROM Option o WHERE o.question.id = :questionId")
    List<Option> findAllByQuestionId(@Param("questionId") Long questionId);
}
