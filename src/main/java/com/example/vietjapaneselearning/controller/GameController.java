package com.example.vietjapaneselearning.controller;

import com.example.vietjapaneselearning.dto.*;
import com.example.vietjapaneselearning.dto.response.StartGameResponse;
import com.example.vietjapaneselearning.service.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {
    @Autowired
    private IGameService gameService;

    @PostMapping("/add_question/{topicId}")
    public ResponseEntity<List<QuestionDTO>> addQuestion(@RequestBody List<QuestionDTO> questionDTO, @PathVariable(name = "topicId") Long topicId) {
        return ResponseEntity.ok(gameService.addQuestion(questionDTO, topicId));
    }

    @PostMapping("/submit_answer")
    public ResponseEntity<AnswerResultDTO> submitAnswer(@RequestBody AnswerDTO dto) {
        AnswerResultDTO result = gameService.submitAnswer(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{typeGame}/start/{lessonId}")
    public ResponseEntity<StartGameResponse> startGame(
            @PathVariable String typeGame,
            @PathVariable Long lessonId
    ) {
        StartGameResponse response = gameService.startGame(typeGame, lessonId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> findByLessonId(@RequestParam(value = "lessonId", required = false) Long lessonId, @RequestParam(value = "page", defaultValue = "0", required = false) int page) {
        PageRequest pageRequest = PageRequest.of(page, 5);
        Page<GameDTO> dtoPage = gameService.findGameByLessonId(lessonId, pageRequest);
        Map<String, Object> map = new HashMap<>();
        map.put("games", dtoPage.getContent());
        map.put("totalPage", dtoPage.getTotalPages());
        return ResponseEntity.ok(map);
    }

    @GetMapping("/detail/{lessonId}/{gameId}")
    public ResponseEntity<List<QuestionDTO>> findDetailsGame(@PathVariable Long lessonId, @PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.findQuestionByLessonIdAndGameId(lessonId, gameId));
    }

    @DeleteMapping("/delete/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recent-activities")
    public ResponseEntity<List<RecentActivityDTO>> recentActivities() {
        return ResponseEntity.ok(gameService.getRecentActivities());
    }
}
