//package com.example.vietjapaneselearning.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class ArrangeSentenceTranslation {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String explanation;
//
//    @ManyToOne
//    @JoinColumn(name = "question_id")
//    private MultipleChoiceQuestion question;
//
//    @ManyToOne
//    @JoinColumn(name = "language_id")
//    private Language language;
//
//}
