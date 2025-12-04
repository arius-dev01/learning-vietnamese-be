package com.example.vietjapaneselearning.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.vietjapaneselearning.model.*;
import com.example.vietjapaneselearning.repository.*;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.vietjapaneselearning.dto.LessonDTO;
import com.example.vietjapaneselearning.dto.VocabularyDTO;
import com.example.vietjapaneselearning.service.ILessonService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class LessonServiceImpl implements ILessonService {
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private VocabularyRepository vocabularyRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerGameRepository playerGameRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<LessonDTO> findAll(String title, String level, Pageable pageable) {
        Page<Lesson> lessons = lessonRepository.findByTitleAndLevel(title, level.toLowerCase(), pageable);
        return lessons.map(lesson -> {
            Long gameCount = gameRepository.countGameByLesson(lesson.getId());
            List<String> gameType = gameRepository.countGameTypeByLesson(lesson.getId());
            return LessonDTO.builder()
                    .id(lesson.getId())
                    .level(lesson.getLevel())
                    .gameCount(gameCount)
                    .title(lesson.getTitle())
                    .typeGame(gameType)
                    .time(lesson.getTime())
                    .content(lesson.getContent())
                    .created(lesson.getCreated())
                    .updated(lesson.getUpdated())
                    .describe(lesson.getDescription())
                    .describeJa(lesson.getDescriptionJa())
                    .contentJa(lesson.getContentJa())
                    .titleJa(lesson.getTitleJa())
                    .build();
        });
    }

    @Override
    public Page<LessonDTO> findAllByUser(String title, String level, Pageable pageable) {
        Page<Lesson> lessons = lessonRepository.findByTitleAndLevel(title, level.toLowerCase(), pageable);
        User user = currentUserService.getUserCurrent();
        String language = user.getLanguage();
//        return lessons.map(lesson -> {
////            UserLessonProgress userLessonProgress = userLessonProgressRepository
////                    .findByUserIdAndLessonId(currentUserService.getUserCurrent().getId(), lesson.getId());
//            Long gameCount = gameRepository.countGameByLesson(lesson.getId());
//
//            return LessonDTO.builder()
//                    .id(lesson.getId())
//                    .level(lesson.getLevel())
//                    .gameCount(gameCount)
//                    .title(lesson.getTitle())
//                    .time(lesson.getTime())
//                    .progress(progressLesson(lesson.getId()))
//                    .content(lesson.getContent())
//                    .created(lesson.getCreated())
//                    .updated(lesson.getUpdated())
//                    .describe(lesson.getDescription())
//
//                    .build();
//        });
        return lessons.map(lesson -> mapperLesson(lesson, language));
    }

//    private LessonDTO translateLesson(String language) {
//        if (language.equals("Japan")) {
//
//        }
//    }

    private LessonDTO mapperLesson(Lesson lesson, String language) {
        LessonDTO lessonDTO = new LessonDTO();
        Long gameCount = gameRepository.countGameByLesson(lesson.getId());

        lessonDTO.setId(lesson.getId());
        lessonDTO.setLevel(lesson.getLevel());
        if (language.equals("Japan")) {
            lessonDTO.setTitle(lesson.getTitleJa());
            lessonDTO.setContent(lesson.getContentJa());
            lessonDTO.setDescribe(lesson.getDescriptionJa());
        } else if (language.equals("English")) {
            lessonDTO.setTitle(lesson.getTitle());
            lessonDTO.setContent(lesson.getContent());
            lessonDTO.setDescribe(lesson.getDescription());
        } else {
            lessonDTO.setTitle(lesson.getTitle());
            lessonDTO.setContent(lesson.getContent());
            lessonDTO.setDescribe(lesson.getDescription());
        }
        lessonDTO.setProgress(progressLesson(lesson.getId()));
        lessonDTO.setCreated(lesson.getCreated());
        lessonDTO.setGameCount(gameCount);
        lessonDTO.setUpdated(lesson.getUpdated());
        return lessonDTO;

    }

    private Long progressLesson(Long lessonId) {
        Long playerGame = playerGameRepository.countCompletedGamesByLesson(currentUserService.getUserCurrent().getId(), lessonId);
        List<Game> game = gameRepository.findByLessonId(lessonId);

//        double tmp = 33.3333333333;
        double tmp = (double) 100 / game.size();
        if (playerGame > 0) {
            return Math.round(tmp * playerGame);
        }
        return 0L;
    }

    @Override
    public LessonDTO addLesson(LessonDTO lessonDTO) {
        if (lessonRepository.findByTitle(lessonDTO.getTitle()).isPresent()) {
            throw new IllegalArgumentException("Lesson is already exist on database. Please choice title different!");
        }
        Lesson lesson = Lesson.builder()
                .title(lessonDTO.getTitle())
                .description(lessonDTO.getDescribe())
                .content(lessonDTO.getContent())
                .updated(LocalDateTime.now())
                .created(LocalDateTime.now())
                .time(lessonDTO.getTime())
                .level(lessonDTO.getLevel())
                .build();
        lessonRepository.save(lesson);

        List<Vocabulary> vocabularies = lessonDTO.getVocabularies().stream().map(item -> Vocabulary.builder()
                .word(item.getWord())
                .meaning(item.getMeaning())
                .lesson(lesson)
                .build()).toList();
        vocabularyRepository.saveAll(vocabularies);
        return lessonDTO;
    }

    @Override
    public LessonDTO findLessonByTitle(String title) {
        User user = currentUserService.getUserCurrent();
        String language = user.getLanguage();
        String newTitle = title.replace("-", " ");
        Lesson lesson = lessonRepository.findByTitle(newTitle)
                .orElse(null);

        List<VocabularyDTO> vocabularies = lesson.getVocabularies().stream().map(item -> {
            return VocabularyDTO.builder()
                    .word(item.getWord())
                    .meaning(language.equals("Japan") ? item.getMeaningJa() : item.getMeaning())
                    .pronunciation(item.getPronunciation())
                    .build();
        }).toList();
        return LessonDTO.builder()
                .id(lesson.getId())
                .level(lesson.getLevel())
                .content(language.equals("Japan") ? lesson.getContentJa() : lesson.getContent())
                .title(language.equals("Japan") ? lesson.getTitleJa() : lesson.getTitle())
                .time(lesson.getTime())
                .describe(language.equals("Japan") ? lesson.getDescriptionJa() : lesson.getDescription())
                .vocabularies(vocabularies)
                .build();
    }

    @Override
    public LessonDTO updateLesson(LessonDTO lessonDTO) {
        Lesson lesson = lessonRepository.findById(lessonDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Lesson with id " + lessonDTO.getId() + " not found!"));

        if (!lessonDTO.getTitle().equals(lesson.getTitle())) {
           Optional<Lesson> checkTitle = lessonRepository.findByTitle(lessonDTO.getTitle());
           if(checkTitle.isPresent()) {
               throw new IllegalArgumentException("Lesson is already exist on database. Please choice title different!");
           }
        }
        lesson.setTitle(lessonDTO.getTitle());
        lesson.setTitleJa(lessonDTO.getTitleJa());
        lesson.setDescription(lessonDTO.getDescribe());
        lesson.setContent(lessonDTO.getContent());
        lesson.setContentJa(lessonDTO.getContentJa());
        lesson.setDescriptionJa(lessonDTO.getDescribeJa());
        lesson.setLevel(lessonDTO.getLevel());
        lesson.setVideo_url(lessonDTO.getVideo_url());
        lesson.setUpdated(LocalDateTime.now());
        lessonRepository.save(lesson);
        return lessonDTO;
    }

    @Override
    @Transactional
    public void deleteLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson with id " + id + " not found!"));
//        vocabularyRepository.deleteByLessonId(lesson.getId());

        lessonRepository.delete(lesson);
    }


    @Override
    public List<LessonDTO> getTop10LessonCompleted() {
        List<Object[]> results = playerGameRepository.getTop10LessonCompleted();
        Long totalUser = userRepository.totalUser();
        List<LessonDTO> lessonDTOs = new ArrayList<>();
        for (Object[] obj : results) {
            Long lessonId = (Long) obj[0];
            Long completedCount = (Long) obj[1];
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new EntityNotFoundException("Lesson with id " + lessonId + " not found!"));
            LessonDTO lessonDTO = LessonDTO.builder()
                    .id(lessonId)
                    .title(lesson.getTitle())
                    .totalUser(totalUser)
                    .countCompleted(completedCount)
                    .build();
            lessonDTOs.add(lessonDTO);
        }
        return lessonDTOs;
    }

//    private void importVocabularyFromExcel(Long lessonId, String filePath) {
//        Lesson lesson = lessonRepository.findById(lessonId)
//                .orElseThrow(() -> new EntityNotFoundException("Lesson with id " + lessonId + " not found!"));
//        List<Vocabulary> vocabularies = new ArrayList<>();
//        try (FileInputStream fileInputStream = new FileInputStream(filePath);
//                Workbook workbook = new XSSFWorkbook(fileInputStream)) {
//            Sheet sheet = workbook.getSheetAt(0);
//            boolean firstRow = true;
//            for (Row row : sheet) {
//                if (firstRow) {
//                    firstRow = false;
//                    continue;
//                }
//
//                String word = row.getCell(0).getStringCellValue();
//                String meaning = row.getCell(1).getStringCellValue();
//                String pronunciation = row.getCell(2).getStringCellValue();
//
//            }
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
