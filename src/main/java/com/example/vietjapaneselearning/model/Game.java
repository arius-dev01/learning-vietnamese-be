package com.example.vietjapaneselearning.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//
//    private String title;
//
//    private String type; // MC, FILL_BLANK, ...
//    private String description;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<MultipleChoiceQuestion> multipleChoiceQuestions;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ArrangeSentence> arrangeSentences;

    @ManyToOne
    @JoinColumn(name = "game_type_id")
    private GameType gameType;

    @OneToMany(mappedBy = "gameId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PlayerGame>  playerGames;

    @OneToMany(mappedBy = "gameId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PlayerAnswer> playerAnswers;

}
