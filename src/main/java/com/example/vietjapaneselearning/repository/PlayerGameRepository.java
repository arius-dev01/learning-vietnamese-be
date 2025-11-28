package com.example.vietjapaneselearning.repository;

import com.example.vietjapaneselearning.model.PlayerGame;
import com.example.vietjapaneselearning.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerGameRepository extends JpaRepository<PlayerGame, Long> {
    @Query("""
                SELECT pg FROM PlayerGame pg
                WHERE pg.userId.id = :userId
                  AND pg.gameId.id = :gameId
                  AND pg.lesson.id = :lessonId
                  AND pg.completed = false
                ORDER BY pg.startAt DESC
            """)
    Optional<PlayerGame> findTopByUserIdAndGameIdAndLessonIdAndCompletedFalse(
            @Param("userId") Long userId,
            @Param("gameId") Long gameId,
            @Param("lessonId") Long lessonId
    );

//    Optional<PlayerGame> findTopByLessonIdAndUserIdAndGameIdOrderByStartAtDesc(Long userId, Long gameId, Long lessonId);

    @Query("SELECT MAX(pg.totalScore) FROM PlayerGame pg WHERE pg.userId.id = :userId AND pg.completed = true GROUP BY  pg.gameId")
    List<Long> findMaxScoresByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(DISTINCT pg.gameId) FROM PlayerGame pg WHERE pg.userId.id = :userId")
    List<Long> countGamesPlayed(@Param("userId") Long userId);

    @Query("SELECT COUNT(DISTINCT pg.lesson) FROM PlayerGame pg WHERE pg.userId.id = :userId AND pg.completed = true")
    List<Long> countLessonPlayed(@Param("userId") Long userId);

    @Query("""
                SELECT COUNT(DISTINCT pg.gameId)
                FROM PlayerGame pg
                WHERE pg.userId.id = :userId
                  AND pg.lesson.id = :lessonId
                  AND pg.completed = true
            """)
    Long countCompletedGamesByLesson(
            @Param("userId") Long userId,
            @Param("lessonId") Long lessonId
    );
    @Query("SELECT p FROM PlayerGame p ORDER BY p.startAt DESC")
    List<PlayerGame> findRecentGames(Pageable pageable);
    @Query("SELECT pg.lesson.id, count(DISTINCT(pg.userId.id)) from PlayerGame pg where pg.completed =true group by  pg.lesson.id")
    List<Object[]>  getTop10LessonCompleted();

}
