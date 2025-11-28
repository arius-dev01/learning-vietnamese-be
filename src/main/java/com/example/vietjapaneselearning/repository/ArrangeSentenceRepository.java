package com.example.vietjapaneselearning.repository;

import com.example.vietjapaneselearning.model.ArrangeSentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArrangeSentenceRepository extends JpaRepository<ArrangeSentence, Long> {
    //    List<ArrangeSentence> findByTopic(Topic topic);
    int countByGameIdAndLessonId(Long gameId, Long lessonId);

//    List<ArrangeSentence> findByGameIdAndLessonId(Long gameId, Long lessonId);
    @Query("SELECT m FROM ArrangeSentence m where m.lesson.id = :lessonId AND m.game.id = :gameId")
    List<ArrangeSentence> findByLessonIdAndGameId(@Param("lessonId") Long lessonId, @Param("gameId") Long gameId);

}
