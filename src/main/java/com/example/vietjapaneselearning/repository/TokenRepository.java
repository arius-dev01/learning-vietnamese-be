package com.example.vietjapaneselearning.repository;

import com.example.vietjapaneselearning.model.Token;
import com.example.vietjapaneselearning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("SELECT t FROM Token t WHERE t.refreshToken = :refresh_token")
    Optional<Token> findRefreshToken(@Param("refresh_token") String refresh_token);
    List<Token> findByUser(User user);
    @Query("SELECT t FROM Token t WHERE t.refreshToken = :refresh_token AND t.expiryDate > CURRENT TIMESTAMP")
    boolean findValidRefreshToken(@Param("refresh_token") String refresh_token);

}
