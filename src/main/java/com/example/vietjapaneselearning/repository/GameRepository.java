package com.example.vietjapaneselearning.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.vietjapaneselearning.model.Game;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT g from Game g where (:lessonId is null or g.lesson.id = :lessonId)")
    Page<Game> findByLessonId(@Param("lessonId") Long lessonId, Pageable pageable);

    @Query("SELECT m FROM Game m join m.gameType gt where m.lesson.id = :lessonId AND gt.type = :typeGame")
    Game findByLessonIdAndTypeGame(@Param("lessonId") Long lessonId, @Param("typeGame") String typeGame);

    @Query("SELECT COUNT(g) FROM Game g WHERE g.lesson.id = :lessonId")
    Long countGameByLesson(@Param("lessonId") Long lessonId);
    @Query("select g.gameType.type from Game g where g.lesson.id = :lessonId")
    List<String> countGameTypeByLesson(@Param("lessonId") Long lessonId);
    @Query("SELECT g from Game g where (:lessonId is null or g.lesson.id = :lessonId)")
    List<Game> findByLessonId(@Param("lessonId") Long lessonId);

}
