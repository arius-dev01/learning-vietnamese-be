package com.example.vietjapaneselearning.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    private Long questionId;
    @ManyToOne()
    @JoinColumn(name = "game_id")
    private Game gameId;
    private String userAnswer;
    private int point;
    private boolean isCorrect;
    @ManyToOne
    @JoinColumn(name = "player_game_id")
    private PlayerGame playerGame;
    private LocalDateTime answeredAt;
}
