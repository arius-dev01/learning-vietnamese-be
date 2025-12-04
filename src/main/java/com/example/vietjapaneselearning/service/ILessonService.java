package com.example.vietjapaneselearning.service;

import com.example.vietjapaneselearning.dto.LessonDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ILessonService {
    Page<LessonDTO> findAll(String title, String level,  Pageable pageable);
    Page<LessonDTO> findAllByUser(String title, String level,  Pageable pageable);
    LessonDTO addLesson(LessonDTO lessonDTO);
    LessonDTO findLessonByTitle(String title);
    LessonDTO updateLesson(LessonDTO lessonDTO);
    void deleteLessonById(Long id);
    List<LessonDTO> getTop10LessonCompleted();
}
