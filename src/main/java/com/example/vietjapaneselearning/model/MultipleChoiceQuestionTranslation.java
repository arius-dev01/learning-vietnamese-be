//package com.example.vietjapaneselearning.model;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.List;
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class MultipleChoiceQuestionTranslation {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String questionText;
//    private String explanation;
//
//    @ManyToOne
//    @JoinColumn(name = "question_id")
//    private MultipleChoiceQuestion question;
//
//    @ManyToOne
//    @JoinColumn(name = "language_id")
//    private Language language;
//}
