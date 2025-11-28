package com.example.vietjapaneselearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.vietjapaneselearning.model.GameType;

public interface GameTypeRepository extends JpaRepository<GameType, Long> {
    GameType findByName(String typeGame);

    @Query("SELECT g from GameType g where g.type = :typeGame")
    GameType findByType(@Param("typeGame") String typeGame);
}
