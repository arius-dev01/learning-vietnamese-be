package com.example.vietjapaneselearning.service;

import com.example.vietjapaneselearning.dto.VocabularyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IVocabularyService {
    Page<VocabularyDTO> getVocabulary(String name,Long lessonId, Pageable pageable);
    VocabularyDTO updateVocabulary(VocabularyDTO vocabularyDTO);
    List<VocabularyDTO> importExcel(MultipartFile file);
    List<VocabularyDTO> addVocabulary(List<VocabularyDTO> vocabularyDTO, Long lessonId);
    void deleteVocabulary(Long id);


}
