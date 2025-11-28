package com.example.vietjapaneselearning.service.impl;

import com.example.vietjapaneselearning.dto.VocabularyDTO;
import com.example.vietjapaneselearning.model.Lesson;
import com.example.vietjapaneselearning.model.Vocabulary;
import com.example.vietjapaneselearning.repository.LessonRepository;
import com.example.vietjapaneselearning.repository.VocabularyRepository;
import com.example.vietjapaneselearning.service.IVocabularyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class VocabularyServiceImpl implements IVocabularyService {
    @Autowired
    private VocabularyRepository vocabularyRepository;
    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public Page<VocabularyDTO> getVocabulary(String word, Long lessonId, Pageable pageable) {
        return vocabularyRepository.findByWordAndLessonId(word, lessonId, pageable)
                .map(item -> {
                    return VocabularyDTO.builder()
                            .id(item.getId())
                            .word(item.getWord())
                            .meaning(item.getMeaning())
                            .pronunciation(item.getPronunciation())
                            .lesson(item.getLesson().getTitle())
                            .build();
                });
    }

    @Override
    public VocabularyDTO updateVocabulary(VocabularyDTO vocabularyDTO) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabularyDTO.getId()).orElseThrow(
                () -> new EntityNotFoundException("Vocabulary Not Found"));
        vocabulary.setWord(vocabularyDTO.getWord());
        vocabulary.setMeaning(vocabularyDTO.getMeaning());
        vocabulary.setPronunciation(vocabularyDTO.getPronunciation());
        vocabularyRepository.save(vocabulary);
        return vocabularyDTO;
    }

    @Override
    public List<VocabularyDTO> importExcel(MultipartFile file) {
        List<VocabularyDTO> vocabularies = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            int current = 0;
            int totalRows = sheet.getLastRowNum();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                VocabularyDTO dto = new VocabularyDTO();
                current++;
                double percent = (double) (current * 100) / totalRows;
                log.info("Import progress: {}%, {}/{}", percent, current, totalRows);
                dto.setWord(row.getCell(0).getStringCellValue());
                dto.setMeaning(row.getCell(1).getStringCellValue());
                dto.setPronunciation(row.getCell(2).getStringCellValue());
                vocabularies.add(dto);
            }
            return vocabularies;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<VocabularyDTO> addVocabulary(List<VocabularyDTO> vocabularyDTO, Long lessonId) {
        List<Vocabulary> vocabularyList = new ArrayList<>();
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson Not Found"));
        for (VocabularyDTO vbDTO : vocabularyDTO) {
            Vocabulary vocabulary = Vocabulary.builder()
                    .word(vbDTO.getWord())
                    .meaning(vbDTO.getMeaning())
                    .pronunciation(vbDTO.getPronunciation())
                    .lesson(lesson)
                    .build();
            vocabularyList.add(vocabulary);
        }
        vocabularyRepository.saveAll(vocabularyList);
        return vocabularyDTO;
    }

    @Override
    public void deleteVocabulary(Long id) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson Not Found"));
        vocabularyRepository.delete(vocabulary);
    }
}
