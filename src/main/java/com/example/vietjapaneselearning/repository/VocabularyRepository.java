package com.example.vietjapaneselearning.repository;

import com.example.vietjapaneselearning.model.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    @Query("SELECT v FROM Vocabulary v WHERE (:word is null or v.word LIKE  CONCAT('%',:word ,'%')) AND (:lessonId is null or v.lesson.id = :lessonId) ")
    Page<Vocabulary> findByWordAndLessonId(@Param("word") String word,@Param("lessonId") Long lessonId, Pageable pageable);
}
