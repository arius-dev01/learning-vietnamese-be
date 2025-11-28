package com.example.vietjapaneselearning.repository;

import com.example.vietjapaneselearning.model.PlayerAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

public interface PlayerAnswerRepository extends JpaRepository<PlayerAnswer, Long> {
    @Query("SELECT COUNT(p) FROM PlayerAnswer p WHERE p.playerGame.id = :playerId AND p.gameId.id = :gameId")
    int countByPlayerIdAndTopicIdAndGameId(@Param("playerId") Long playerId, @Param("gameId") Long gameId);

    @Query("SELECT COALESCE(SUM(p.point), 0) FROM PlayerAnswer p WHERE p.playerGame.id = :playerId AND p.gameId.id = :gameId")
    int sumPointByPlayerIdAndGameId(@Param("playerId") Long playerId, @Param("gameId") Long gameId);

//    @Query("""
//                SELECT p FROM PlayerAnswer p
//                WHERE p.playerGame.userId = :userId
//                  AND p.gameId = :gameId
//                  AND p.questionId = :questionId
//            """)
//    Optional<PlayerAnswer> findByUserIdAndGameIdAndQuestionId(
//            @Param("userId") Long userId,
//            @Param("gameId") Long gameId,
//            @Param("questionId") Long questionId
//    );

    @Query("SELECT COALESCE(SUM(p.point), 0) FROM PlayerAnswer p WHERE p.playerGame.id = :playerGameId")
    int sumPointByPlayerGameId(@Param("playerGameId") Long playerGameId);

}
