package com.example.vietjapaneselearning.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "arrange_sentence")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArrangeSentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sentence;
    private String description;
    private String descriptionJa;
    //    @ManyToOne
//    @JoinColumn(name = "topic_id")
//    private Topic topic;
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    private LocalDateTime createdAt;

}
