package com.example.vietjapaneselearning.repository;

import com.example.vietjapaneselearning.model.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("SELECT l FROM Lesson l WHERE (:title IS NULL OR l.title LIKE %:title% OR l.titleJa LIKE %:title%)  AND (:level IS NULL OR l.level  like %:level% ) ")
    Page<Lesson> findByTitleAndLevel(@Param("title") String title, @Param("level") String level, Pageable pageable);
    @Query("SELECT l from Lesson l where l.title = :title OR l.titleJa = :title")
    Optional<Lesson> findByTitle(@Param("title") String title);
    Optional<Lesson> findByTitleJa(String title);



}
