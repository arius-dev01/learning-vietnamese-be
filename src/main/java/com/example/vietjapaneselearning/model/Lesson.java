package com.example.vietjapaneselearning.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;
    private String titleJa;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String descriptionJa;

    @Column
    private String video_url;

    @Column
    private String level;
    @Column
    private String content;
    private String contentJa;
    @Column
    private LocalDateTime time;

    @Column
    private LocalDateTime created;
    @Column
    private LocalDateTime updated;
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vocabulary> vocabularies;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerAnswer> playerAnswers;
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerGame> playerGames;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Game> games;


    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArrangeSentence> arrangeSentences;



    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MultipleChoiceQuestion> multipleChoiceQuestions;
}
