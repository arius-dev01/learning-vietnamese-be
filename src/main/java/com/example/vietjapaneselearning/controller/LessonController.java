package com.example.vietjapaneselearning.controller;

import com.example.vietjapaneselearning.dto.LessonDTO;
import com.example.vietjapaneselearning.service.ILessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lesson")
public class LessonController {
    @Autowired
    private ILessonService lessonService;
    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(name = "title", required = false) String title,
                                                        @RequestParam(name = "level", required = false) String level,
                                                        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                       @RequestParam(name = "size", required = false, defaultValue = "5") int size){
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"title"));
        Map<String, Object> response = new HashMap<>();
        Page<LessonDTO> lessonDTOPage = lessonService.findAll(title, level, pageRequest);
        response.put("lesson", lessonDTOPage.getContent());
        response.put("totalPage", lessonDTOPage.getTotalPages());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/findByUser")
    public ResponseEntity<Map<String, Object>> findAllByUser(@RequestParam(name = "title", required = false) String title,
                                                       @RequestParam(name = "level", required = false) String level,
                                                       @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                       @RequestParam(name = "size", required = false, defaultValue = "5") int size){
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id"));
        Map<String, Object> response = new HashMap<>();
        Page<LessonDTO> lessonDTOPage = lessonService.findAllByUser(title, level, pageRequest);
        response.put("lesson", lessonDTOPage.getContent());
        response.put("totalPage", lessonDTOPage.getTotalPages());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/add_lesson")
    public ResponseEntity<LessonDTO> addLesson(@RequestBody LessonDTO lessonDTO){
        return ResponseEntity.ok(lessonService.addLesson(lessonDTO));
    }

    @GetMapping("/findByTitle/{title}")
    public ResponseEntity<LessonDTO> findByTitle(@PathVariable("title") String title){
        return ResponseEntity.ok(lessonService.findLessonByTitle(title));
    }

    @PutMapping("/update_lesson")
    public ResponseEntity<LessonDTO> updateLesson(@RequestBody LessonDTO lessonDTO){
        return ResponseEntity.ok(lessonService.updateLesson(lessonDTO));
    }

    @DeleteMapping("/delete_lesson/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable("id") Long id){
        lessonService.deleteLessonById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/top-lesson")
    public ResponseEntity<List<LessonDTO>> getTopLesson(){
        return ResponseEntity.ok(lessonService.getTop10LessonCompleted());
    }
}
