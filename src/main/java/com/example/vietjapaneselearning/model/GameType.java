package com.example.vietjapaneselearning.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_type")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GameType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String type;
    private String description;
}
