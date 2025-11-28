//package com.example.vietjapaneselearning.model;
//import jakarta.persistence.*;
//import lombok.*;
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class LessonTranslation {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String title;
//    private String description;
//    private String content;
//    @ManyToOne
//    @JoinColumn(name = "lesson_id")
//    private Lesson lesson;
//
//    @ManyToOne
//    @JoinColumn(name = "language_id")
//    private Language language;
//}
