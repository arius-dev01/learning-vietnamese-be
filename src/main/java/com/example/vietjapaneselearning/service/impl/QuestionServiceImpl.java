package com.example.vietjapaneselearning.service.impl;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.vietjapaneselearning.dto.OptionDTO;
import com.example.vietjapaneselearning.dto.QuestionDTO;
import com.example.vietjapaneselearning.model.ArrangeSentence;
import com.example.vietjapaneselearning.model.Game;
import com.example.vietjapaneselearning.model.GameType;
import com.example.vietjapaneselearning.model.Lesson;
import com.example.vietjapaneselearning.model.MultipleChoiceQuestion;
import com.example.vietjapaneselearning.model.Option;
import com.example.vietjapaneselearning.repository.ArrangeSentenceRepository;
import com.example.vietjapaneselearning.repository.GameRepository;
import com.example.vietjapaneselearning.repository.GameTypeRepository;
import com.example.vietjapaneselearning.repository.LessonRepository;
import com.example.vietjapaneselearning.repository.MultipleChoiceGameQuestionRepository;
import com.example.vietjapaneselearning.repository.OptionRepository;
import com.example.vietjapaneselearning.service.IQuestionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuestionServiceImpl implements IQuestionService {
    @Autowired
    private ArrangeSentenceRepository arrangeSentenceRepository;
    @Autowired
    private MultipleChoiceGameQuestionRepository multipleChoiceGameQuestionRepository;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private GameTypeRepository gameTypeRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public List<QuestionDTO> addQuestion(String typeGame, Long lessonId, List<QuestionDTO> questionDTO) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(EntityNotFoundException::new);
        GameType gameType = gameTypeRepository.findByType(typeGame);
        if (gameType == null)
            throw new EntityNotFoundException("Game type not found");
        Game game = Game.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .gameType(gameType)
                .lesson(lesson)
                .build();

        if (gameType.getType().equals("MC") || gameType.getType().equals("LS")) {
            for (QuestionDTO dto : questionDTO) {
                MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
                multipleChoiceQuestion.setQuestionText(dto.getQuestionText());
                multipleChoiceQuestion.setExplanation(dto.getExplanation());
                multipleChoiceQuestion.setQuestionTextJa(dto.getQuestionTextJa());
                multipleChoiceQuestion.setExplanationJa(dto.getExplanationJa());
                if (game.getGameType().getType().equals("LS")) {
                    multipleChoiceQuestion.setAudioUrl(true);
                }
                multipleChoiceQuestion.setLesson(lesson);
                gameRepository.save(game);
                multipleChoiceQuestion.setGame(game);
                multipleChoiceGameQuestionRepository.save(multipleChoiceQuestion);
                List<Option> options = new ArrayList<>();
                for (OptionDTO optionDTO : dto.getOptions()) {
                    Option option = new Option();
                    option.setText(optionDTO.getContent());
                    option.setCorrect(optionDTO.isCorrect());
                    option.setQuestion(multipleChoiceQuestion);
                    options.add(option);
                }
                optionRepository.saveAll(options);
            }
        } else if (gameType.getType().equals("AS")) {
            for (QuestionDTO dto : questionDTO) {
                ArrangeSentence arrangeSentence = new ArrangeSentence();
                arrangeSentence.setSentence(String.join(" ", dto.getSentence()));
                arrangeSentence.setDescription(dto.getExplanation());
                arrangeSentence.setDescriptionJa(dto.getExplanationJa());
                arrangeSentence.setLesson(lesson);
                gameRepository.save(game);
                arrangeSentence.setGame(game);
                arrangeSentenceRepository.save(arrangeSentence);
            }
        }

        return questionDTO;
    }

    @Override
    public List<QuestionDTO> updateQuestion(Long gameId, Long lessonId, List<QuestionDTO> questions) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(EntityNotFoundException::new);

        if (game.getGameType().getType().equals("MC") || game.getGameType().getType().equals("LS")) {
            updateMultipleChoiceGameQuestion(gameId, lessonId, questions);
        } else {
            updateArrangeSentence(gameId, lessonId, questions);
        }

        return questions;
    }

    private void updateMultipleChoiceGameQuestion(Long gameId, Long lessonId, List<QuestionDTO> questions) {
        List<MultipleChoiceQuestion> multipleChoiceQuestions = new ArrayList<>();
        List<Option> options = new ArrayList<>();
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(EntityNotFoundException::new);
//        GameType gameType = gameTypeRepository.findById(gameId)
//                .orElseThrow(EntityNotFoundException::new);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));
        for (QuestionDTO questionDTO : questions) {
            MultipleChoiceQuestion multipleChoiceQuestion;
            if (questionDTO.getQuestionText() == null || questionDTO.getQuestionText().isBlank()) continue;
            if (questionDTO.getQuestionId() != null && questionDTO.getQuestionId() != 0) {
                multipleChoiceQuestion = multipleChoiceGameQuestionRepository.findById(questionDTO.getQuestionId())
                        .orElseThrow(() -> new EntityNotFoundException("Not found questionId"));
                if (questionDTO.getQuestionText() != null && !questionDTO.getQuestionText().isBlank()) {
                    multipleChoiceQuestion.setQuestionText(questionDTO.getQuestionText());
                }
                if (questionDTO.getExplanation() != null && !questionDTO.getExplanation().isBlank()) {
                    multipleChoiceQuestion.setExplanation(questionDTO.getExplanation());
                }
                if(questionDTO.getExplanationJa() != null && !questionDTO.getExplanationJa().isBlank()){
                    multipleChoiceQuestion.setExplanationJa(questionDTO.getExplanationJa());
                }
                if(questionDTO.getQuestionTextJa() != null && !questionDTO.getQuestionTextJa().isBlank()){
                    multipleChoiceQuestion.setQuestionTextJa(questionDTO.getQuestionTextJa());
                }
//                if (questionDTO.getAudio_url() != null && !questionDTO.getExplanation().isBlank()) {
//                    multipleChoiceQuestion.setAudioUrl(questionDTO.getAudio_url());
//                }

                if(questionDTO.isAudio_url()){
                    multipleChoiceQuestion.setAudioUrl(true);
                }
                if (questionDTO.getOptions() != null && !questionDTO.getOptions().isEmpty()) {
                    for (OptionDTO optionDTO : questionDTO.getOptions()) {
                        Option option = optionRepository.findById(optionDTO.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Not found optionId"));
                        option.setText(optionDTO.getContent());
                        option.setCorrect(optionDTO.isCorrect());
                        options.add(option);
                    }
                }
                multipleChoiceQuestions.add(multipleChoiceQuestion);
            } else {
                multipleChoiceQuestion = MultipleChoiceQuestion.builder()
                        .questionText(questionDTO.getQuestionText())
                        .explanation(questionDTO.getExplanation())
                        .questionTextJa(questionDTO.getQuestionTextJa())
                        .explanationJa(questionDTO.getExplanationJa())
                        .lesson(lesson)
//                        .audioUrl(questionDTO.getAudio_url() != null && !questionDTO.getAudio_url().isBlank()
//                                ? questionDTO.getAudio_url()
//                                : null)
                        .audioUrl(questionDTO.isAudio_url())
                        .game(game)
                        .build();
                multipleChoiceQuestions.add(multipleChoiceQuestion);
                if (!questionDTO.getOptions().isEmpty()) {
                    for (OptionDTO optionDTO : questionDTO.getOptions()) {
                        Option option = new Option();
                        option.setText(optionDTO.getContent());
                        option.setCorrect(optionDTO.isCorrect());
                        option.setQuestion(multipleChoiceQuestion);
                        options.add(option);
                    }
                }
                multipleChoiceQuestions.add(multipleChoiceQuestion);
            }
        }
        multipleChoiceGameQuestionRepository.saveAll(multipleChoiceQuestions);
        if (!options.isEmpty()) {
            optionRepository.saveAll(options);
        }
    }

    private void updateArrangeSentence(Long gameId, Long lessonId, List<QuestionDTO> questions) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(EntityNotFoundException::new);
//        GameType gameType = gameTypeRepository.findById(gameId)
//                .orElseThrow(EntityNotFoundException::new);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));
        for (QuestionDTO questionDTO : questions) {
            if (questionDTO.getQuestionId() != null && questionDTO.getQuestionId() != 0) {
                ArrangeSentence arrangeSentence = arrangeSentenceRepository.findById(questionDTO.getQuestionId())
                        .orElseThrow(() -> new EntityNotFoundException("Not found questionId"));
                if (questionDTO.getSentence() != null && !questionDTO.getSentence().isEmpty()) {
                    String convertSentence = String.join(" ", questionDTO.getSentence());
                    arrangeSentence.setSentence(convertSentence);
                }
                arrangeSentenceRepository.save(arrangeSentence);
            } else {
                String convertSentence = String.join(" ", questionDTO.getSentence());
                ArrangeSentence arrangeSentence = ArrangeSentence.builder()
                        .sentence(convertSentence)
                        .lesson(lesson)
                        .game(game)
                        .build();
                arrangeSentenceRepository.save(arrangeSentence);
            }
        }
    }

    @Override
    public void deleteQuestion(Long questionId, Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(EntityNotFoundException::new);
        if (game.getGameType().getType().equals("MC") || game.getGameType().getType().equals("LS")) {
            MultipleChoiceQuestion multipleChoiceQuestion = multipleChoiceGameQuestionRepository.findById(questionId)
                    .orElseThrow(() -> new EntityNotFoundException("Not found questionId"));
            multipleChoiceGameQuestionRepository.delete(multipleChoiceQuestion);

        } else {
            ArrangeSentence arrangeSentence = arrangeSentenceRepository.findById(questionId)
                    .orElseThrow(() -> new EntityNotFoundException("Not found questionId"));
            arrangeSentenceRepository.delete(arrangeSentence);
        }
        long remainQuestions = 0;
        if(game.getGameType().getType().equals("MC") || game.getGameType().getType().equals("LS")){
            remainQuestions = multipleChoiceGameQuestionRepository.countByGameId(gameId);
        }else{
            remainQuestions = arrangeSentenceRepository.countByGameId(gameId);
        }
        if(remainQuestions == 0){
            gameRepository.delete(game);
        }

    }

    @Override
    public List<QuestionDTO> importExcelMultipleChoice(MultipartFile file) {
        return importExcel(file, QuestionType.MULTIPLE_CHOICE);
    }

    @Override
    public List<QuestionDTO> importExcelListenChoice(MultipartFile file) {
        return importExcel(file, QuestionType.LISTEN_CHOICE);
    }

    @Override
    public List<QuestionDTO> importExcelArrange(MultipartFile file) {
        List<QuestionDTO> questions = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                QuestionDTO questionDTO = new QuestionDTO();
                if(row.getRowNum() == 0){continue;}
//                questionDTO.setQuestionText(getCellValue(row, 0));
                questionDTO.setSentence(Collections.singletonList(getCellValue(row, 0)));
                questionDTO.setExplanation(getCellValue(row, 1));
                questionDTO.setExplanationJa(getCellValue(row, 2));
                questionDTO.setAudio_url(true);
                questions.add(questionDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }



    private List<QuestionDTO> importExcel(MultipartFile file, QuestionType type) {
        List<QuestionDTO> questions = new ArrayList<>();
        try (InputStream inp = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inp);) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;
                QuestionDTO questionDTO = new QuestionDTO();
                List<OptionDTO> options = new ArrayList<>();
                String correct = getCellValue(row, row.getLastCellNum() - 1);
                switch (type) {
                    case LISTEN_CHOICE -> {
                        questionDTO.setQuestionText(getCellValue(row, 0));
                        questionDTO.setExplanation(getCellValue(row, 1));
                        questionDTO.setExplanationJa(getCellValue(row, 2));
//                        questionDTO.setAudio_url(getCellValue(row, 2));
                        populateOptions(row, options, 3, correct);
                    }
                    case MULTIPLE_CHOICE -> {
                        questionDTO.setQuestionText(getCellValue(row, 0));
                        questionDTO.setExplanation(getCellValue(row, 1));
                        questionDTO.setQuestionTextJa(getCellValue(row, 2));
                        questionDTO.setExplanationJa(getCellValue(row, 3));

                        populateOptions(row, options, 4, correct);
                    }
                }
                questionDTO.setOptions(options);
                questions.add(questionDTO);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }

    private enum QuestionType {
        MULTIPLE_CHOICE,
        LISTEN_CHOICE
    }

    private String getCellValue(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();

            case NUMERIC:
                double num = cell.getNumericCellValue();
                if (num == (long) num) {
                    return String.valueOf((long) num);
                }
                return String.valueOf(num);

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case FORMULA:
                // trả về giá trị formula dưới dạng String
                try {
                    return cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue());
                }

            case BLANK:
            default:
                return "";
        }
    }

    private void populateOptions(Row row, List<OptionDTO> options, int startIndex, String correctOption) {
        for (int i = startIndex; i < row.getLastCellNum() - 1; i++) {
            String cellValue = getCellValue(row, i);
            if (cellValue != null && !cellValue.isEmpty()) {
                OptionDTO optionDTO = new OptionDTO();
                optionDTO.setContent(cellValue);
                char optionChar = (char) ('A' + (i - startIndex));
                optionDTO.setCorrect(correctOption.equalsIgnoreCase(String.valueOf(optionChar)));
                options.add(optionDTO);
            }
        }
    }

}
