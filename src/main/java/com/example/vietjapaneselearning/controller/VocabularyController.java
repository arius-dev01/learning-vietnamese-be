package com.example.vietjapaneselearning.controller;

import com.example.vietjapaneselearning.dto.VocabularyDTO;
import com.example.vietjapaneselearning.service.IVocabularyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vocabulary")
public class VocabularyController {
    @Autowired
    private IVocabularyService vocabularyService;

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getVocabulary(@RequestParam(required = false) String word,
                                                             @RequestParam(required = false, defaultValue = "0") int page,
                                                             @RequestParam(required = false) Long lessonId
    ) {
        Pageable pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "id"));
        Map<String, Object> map = new HashMap<>();
        Page<VocabularyDTO> dtoPage = vocabularyService.getVocabulary(word, lessonId, pageable);
        map.put("vocabularies", dtoPage.getContent());
        map.put("totalPage", dtoPage.getTotalPages());
        return ResponseEntity.ok(map);
    }

    @PutMapping("/update")
    public ResponseEntity<VocabularyDTO> updateVocabulary(@RequestBody VocabularyDTO vocabularyDTO) {
        return ResponseEntity.ok(vocabularyService.updateVocabulary(vocabularyDTO));
    }

    @PostMapping("/add/{lessonId}")
    public ResponseEntity<List<VocabularyDTO>> addVocabulary(@RequestBody List<VocabularyDTO> vocabularyDTO, @PathVariable Long lessonId) {
        return ResponseEntity.ok(vocabularyService.addVocabulary(vocabularyDTO, lessonId));
    }

    @PostMapping("/import")
    public ResponseEntity<List<VocabularyDTO>> importExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(vocabularyService.importExcel(file));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVocabulary(@PathVariable Long id) {
        vocabularyService.deleteVocabulary(id);
        return ResponseEntity.ok().build();
    }
}
