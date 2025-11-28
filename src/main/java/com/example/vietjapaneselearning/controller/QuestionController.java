package com.example.vietjapaneselearning.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.vietjapaneselearning.dto.QuestionDTO;
import com.example.vietjapaneselearning.service.IQuestionService;

@RestController
@RequestMapping("/api/question")
public class QuestionController {
    @Autowired
    private IQuestionService questionService;

    @PutMapping("/update/{gameId}/{lessonId}")
    public ResponseEntity<List<QuestionDTO>> updateQuestion(@PathVariable(name = "gameId") Long gameId,
            @PathVariable(name = "lessonId") Long lessonId, @RequestBody List<QuestionDTO> questionDTO) {
        return ResponseEntity.ok(questionService.updateQuestion(gameId, lessonId, questionDTO));
    }

    @DeleteMapping("/delete/{gameId}/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable(name = "gameId") Long gameId,
            @PathVariable(name = "questionId") Long questionId) {
        questionService.deleteQuestion(questionId, gameId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/import-question-mc")
    public ResponseEntity<List<QuestionDTO>> importQuestionExcelMC(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(questionService.importExcelMultipleChoice(file));
    }

    @PostMapping("/import-question-lc")
    public ResponseEntity<List<QuestionDTO>> importQuestionExcelLC(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(questionService.importExcelListenChoice(file));
    }

    @PostMapping("/import-question-ar")
    public ResponseEntity<List<QuestionDTO>> importQuestionExcelAR(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(questionService.importExcelArrange(file));
    }

    @PostMapping("/add/{typeGame}/{lessonId}")
    public ResponseEntity<List<QuestionDTO>> addQuestion(@PathVariable(name = "typeGame") String typeGame,
            @PathVariable(name = "lessonId") Long lessonId,
            @RequestBody List<QuestionDTO> questionDTO) {
        return ResponseEntity.ok(questionService.addQuestion(typeGame, lessonId, questionDTO));
    }
}
