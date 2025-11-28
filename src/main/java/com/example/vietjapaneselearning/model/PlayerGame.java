package com.example.vietjapaneselearning.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "player_game")
public class PlayerGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startAt;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User userId;
//    private Long topicId;
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game gameId;
    private LocalDateTime updatedAt;
    private Long questionId;
    private int totalScore;
    private int currentStreak;
    private boolean completed;
    private LocalDateTime completedAt;
}
