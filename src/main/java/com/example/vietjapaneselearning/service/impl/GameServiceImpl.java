package com.example.vietjapaneselearning.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.vietjapaneselearning.dto.*;
import com.example.vietjapaneselearning.model.*;
import com.example.vietjapaneselearning.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.vietjapaneselearning.dto.response.StartGameResponse;
import com.example.vietjapaneselearning.service.IGameService;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GameServiceImpl implements IGameService {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private MultipleChoiceGameQuestionRepository multipleChoiceGameQuestionRepository;

    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private PlayerAnswerRepository playerAnswerRepository;
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private PlayerGameRepository playerGameRepository;

    @Autowired
    private LessonRepository lessonRepository;
    private static final int DEFAULT_POINT = 10;
    @Autowired
    private ArrangeSentenceRepository arrangeSentenceRepository;
    @Autowired
    private GameTypeRepository gameTypeRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<QuestionDTO> addQuestion(List<QuestionDTO> dto, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Not fount topic with id: " + lessonId));
        Long gameId = dto.get(0).getGameId();

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Not found with game id " + gameId));
        if (gameId == 1) {
            addMultipleChoiceQuestion(dto, lesson, game);
        } else if (gameId == 3) {
            addMultipleChoiceQuestion(dto, lesson, game);
        } else {
            addArrangeSentence(dto, lesson, game);
        }
        return dto;
    }

    @Override
    public AnswerResultDTO submitAnswer(AnswerDTO answerDTO) {

        Game game = gameRepository.findById(answerDTO.getGameId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found game with id: " + answerDTO.getGameId()));
        AnswerResultDTO result;
        long totalQuestion = 0;
        if ("MC".equals(game.getGameType().getType()) || "LS".equals(game.getGameType().getType())) {
            result = handleMultipleChoiceAnswer(answerDTO);
            totalQuestion = multipleChoiceGameQuestionRepository
                    .countByGameIdAndLessonId(game.getId(), answerDTO.getLessonId());
        } else {
            result = handleArrangeSentence(answerDTO);
            totalQuestion =arrangeSentenceRepository
                    .countByGameIdAndLessonId(game.getId(), answerDTO.getLessonId());
        }

        AnswerResultDTO completed = checkAndCompleteGame(game.getId(), answerDTO.getLessonId(),
                answerDTO.getPlayerId());
        PlayerGame playerGame = playerGameRepository.findById(answerDTO.getPlayerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found player with id: " + answerDTO.getPlayerId()));
        int newTotalScore = playerAnswerRepository.sumPointByPlayerGameId(playerGame.getId());
        result.setNewTotalScore(newTotalScore);
        int streak = playerGame.getCurrentStreak();
        Integer bonusPoint = 0;
        if (streak == totalQuestion) {
            bonusPoint += 20;
            result.setBonus(bonusPoint);
        }
        if (completed != null) {
            result.setTotalScore(completed.getTotalScore());
            result.setComplete(true);
            result.setCurrentStreak(streak);
        }

        return result;
    }

    private void addArrangeSentence(List<QuestionDTO> dto, Lesson lesson, Game game) {
        List<ArrangeSentence> arrangeSentences = new ArrayList<>();

        for (QuestionDTO questionDTO : dto) {
            ArrangeSentence arrangeSentence = ArrangeSentence.builder()
                    .sentence(questionDTO.getAnswerText())
                    .game(game)
                    .createdAt(LocalDateTime.now())
                    .lesson(lesson)
                    // .topic(topic)
                    .build();
            arrangeSentences.add(arrangeSentence);
        }
        arrangeSentenceRepository.saveAll(arrangeSentences);
    }

    private void addMultipleChoiceQuestion(List<QuestionDTO> dto, Lesson lesson, Game game) {
        List<MultipleChoiceQuestion> multipleChoiceQuestions = new ArrayList<>();
        List<Option> ots = new ArrayList<>();
        for (QuestionDTO questionDTO : dto) {
            MultipleChoiceQuestion mc = MultipleChoiceQuestion.builder()
                    .questionText(questionDTO.getQuestionText())
                    .explanation(questionDTO.getExplanation())
                    .image_url(questionDTO.getImage_url())
                    .lesson(lesson)
                    .game(game)
                    .build();
            multipleChoiceQuestions.add(mc);
            if (questionDTO.getOptions() != null) {
                List<Option> options = questionDTO.getOptions().stream()
                        .map(item -> Option.builder()
                                .question(mc)
                                .text(item.getContent())
                                .isCorrect(item.isCorrect())
                                .build())
                        .toList();
                ots.addAll(options);
            }
        }
        multipleChoiceGameQuestionRepository.saveAll(multipleChoiceQuestions);
        optionRepository.saveAll(ots);
    }

    private AnswerResultDTO handleMultipleChoiceAnswer(AnswerDTO answerDTO) {
        MultipleChoiceQuestion mc = multipleChoiceGameQuestionRepository.findById(answerDTO.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found MC question " + answerDTO.getQuestionId()));
        Game game = gameRepository.findById(answerDTO.getGameId())
                .orElseThrow(() -> new EntityNotFoundException());

        List<Option> options = optionRepository.findAllByQuestionId(mc.getId());
        boolean correct = options.stream()
                .anyMatch(opt -> opt.isCorrect() && opt.getId().equals(answerDTO.getOptionId()));
        Long correctOptionId = options.stream()
                .filter(Option::isCorrect)
                .map(Option::getId)
                .findFirst()
                .orElse(null);
        PlayerGame playerGame = playerGameRepository.findById(answerDTO.getPlayerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found player with id: " + answerDTO.getPlayerId()));
        int streak = playerGame.getCurrentStreak();

        if (correct) {
            playerGame.setCurrentStreak(streak + 1);
            int totalPoint = playerGame.getTotalScore() + 10;
            playerGame.setTotalScore(totalPoint);
        }
        Lesson lesson = lessonRepository.findById(answerDTO.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException(""));
        PlayerAnswer playerAnswer = PlayerAnswer.builder()
                .questionId(answerDTO.getQuestionId())
                .gameId(game)
                .playerGame(playerGame)
                .isCorrect(correct)
                .lesson(lesson)
                .userAnswer(answerDTO.getOptionId().toString())
                .answeredAt(LocalDateTime.now())
                .point(correct ? DEFAULT_POINT : 0)
                .build();
        playerAnswerRepository.save(playerAnswer);
        playerGame.setQuestionId(answerDTO.getQuestionId());
        playerGameRepository.save(playerGame);
        return AnswerResultDTO.builder()
                .correct(correct)
                .correctOptionId(correctOptionId)
                .explanation(mc.getExplanation())
                .build();
    }

    private AnswerResultDTO handleArrangeSentence(AnswerDTO answerDTO) {
        ArrangeSentence arrangeSentence = arrangeSentenceRepository.findById(answerDTO.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found arrangeId" + answerDTO.getQuestionId()));
        String word = String.join(" ", answerDTO.getAnswer());
        PlayerGame playerGame = playerGameRepository.findById(answerDTO.getPlayerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found player with id: " + answerDTO.getPlayerId()));
        boolean correct = false;
        if (word.equals(arrangeSentence.getSentence())) {
            correct = true;
        }
        int streak = playerGame.getCurrentStreak();

        if (correct) {
            playerGame.setCurrentStreak(streak + 1);
        }
        Lesson lesson = lessonRepository.findById(answerDTO.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException(""));
        Game game = gameRepository.findById(answerDTO.getGameId())
                .orElseThrow(() -> new EntityNotFoundException());
        PlayerAnswer playerAnswer = PlayerAnswer.builder()
                .questionId(answerDTO.getQuestionId())
                .gameId(game)
                .playerGame(playerGame)
                .isCorrect(correct)
                .lesson(lesson)
                .userAnswer(answerDTO.getAnswer().toString())
                .answeredAt(LocalDateTime.now())
                .point(correct ? DEFAULT_POINT : 0)
                .build();
        playerAnswerRepository.save(playerAnswer);
        playerGame.setQuestionId(answerDTO.getQuestionId());
        playerGameRepository.save(playerGame);
        return AnswerResultDTO.builder()
                .correct(correct)
                // .explanation(mc.getExplanation())
                .build();
    }

    @Override
    public StartGameResponse startGame(String typeGame, Long lessonId) {
        User userId = currentUserService.getUserCurrent();
        String convertTypeGame = typeGame.replace("-", " ");
        GameType gameType = gameTypeRepository.findByName(convertTypeGame);
        Game game = gameRepository.findByLessonIdAndTypeGame(lessonId, gameType.getType());
        if (game == null) {
            throw new EntityNotFoundException(
                    "Not found game with lesson id: " + lessonId + " and type game: " + typeGame);
        }
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Not found topic with id: " + lessonId));
        PlayerGame playerGame = playerGameRepository
                .findTopByUserIdAndGameIdAndLessonIdAndCompletedFalse(userId.getId(), game.getId(), lessonId)
                .orElseGet(() -> {
                    PlayerGame newGame = PlayerGame.builder()
                            .gameId(game)
                            // .topicId(topicId)
                            .lesson(lesson)
                            .currentStreak(0)
                            .userId(userId)
                            .completed(false)
                            .totalScore(0)
                            .startAt(LocalDateTime.now())
                            .build();
                    return playerGameRepository.save(newGame);
                });
        List<QuestionDTO> questions = null;
        // log.info("Game: {}", game);
        User user =currentUserService.getUserCurrent();
        String language = user.getLanguage();
        if (gameType.getType().equals("MC") || gameType.getType().equals("LS")) {
            questions = multipleChoiceGameQuestionRepository
                    .findByLessonIdAndGameId(lessonId, game.getId())
                    .stream()
                    .map(q -> QuestionDTO.builder()
                            .gameId(game.getId())
                            .questionId(q.getId())
                            .audio_url(q.isAudioUrl())
                            .questionText(
                                    language.equals("English")
                                            ? q.getQuestionText()
                                            : (q.getQuestionTextJa() != null ? q.getQuestionTextJa() : q.getQuestionText())
                            )
                            .explanation(language.equals("English") ? q.getExplanation() : q.getExplanationJa())
                            // .image_url(q.getImage_url())
                            .options(optionRepository.findAllByQuestionId(q.getId())
                                    .stream()
                                    .map(o -> new OptionDTO(o.getId(), o.getText(),
                                            o.isCorrect()))
                                    .toList())
                            .build())
                    .toList();
        } else if (gameType.getType().equals("AS")) {
            questions = arrangeSentenceRepository.findByLessonIdAndGameId(lesson.getId(), game.getId())
                    .stream()
                    .map(q -> QuestionDTO.builder()
                            .gameId(q.getGame().getId())
                            .questionId(q.getId())
                            .sentence(Arrays.asList(q.getSentence().split(" ")))
                            .explanation(language.equals("English") ? q.getDescription() : q.getDescriptionJa())
                            .build())
                    .toList();

        }
        return StartGameResponse.builder()
                .gameId(game.getId())
                .playerId(playerGame.getId())
                .lastQuestionId(playerGame.getQuestionId())
                .questions(questions)
                .startTime(playerGame.getStartAt())
                .build();
    }

    @Override
    public Page<GameDTO> findGameByLessonId(Long lessonId, Pageable pageable) {
        Page<Game> game = gameRepository.findByLessonId(lessonId, pageable);
        Long gameCount = gameRepository.countGameByLesson(lessonId);
//        PlayerGame playerGame = playerGameRepository.find
        return game.map(
                it -> {
                    return GameDTO.builder()
                            .id(it.getId())
                            .titleLesson(it.getLesson().getTitle())
                            .title(it.getGameType().getName())
                            .gameTypeId(it.getGameType().getId())
                            .gameCount(gameCount)

                            .lessonId(it.getLesson().getId())
                            .type(it.getGameType().getType())
                            .description(it.getGameType().getDescription())
                            .build();
                });
    }

    @Override
    public List<QuestionDTO> findQuestionByLessonIdAndGameId(Long lessonId, Long gameId) {
        GameType gameType = gameTypeRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Not found topic with id: " + gameId));
        Game game = gameRepository.findByLessonIdAndTypeGame(lessonId, gameType.getType());
        if (gameType.getType().equals("MC") || gameType.getType().equals("LS")) {
            List<MultipleChoiceQuestion> list = multipleChoiceGameQuestionRepository
                    .findByLessonIdAndGameId(lessonId, game.getId());

            return list.stream().map(item -> {
                List<OptionDTO> options = item.getOptions().stream()
                        .map(option -> OptionDTO.builder()
                                .id(option.getId())
                                .content(option.getText())
                                .correct(option.isCorrect())
                                .build())
                        .toList();

                return QuestionDTO.builder()
                        .gameId(game.getId())
                        .lessonId(item.getLesson().getId())
                        .questionText(item.getQuestionText())
                        .explanation(item.getExplanation())
                        .options(options)
                        .questionId(item.getId())
                        .build();
            }).toList();
        } else if (gameType.getType().equals("AS")) {
            List<ArrangeSentence> list = arrangeSentenceRepository.findByLessonIdAndGameId(lessonId,
                    game.getId());

            return list.stream().map(item -> {
                return QuestionDTO.builder()
                        .gameId(gameId)
                        .sentence(Arrays.asList(item.getSentence().split(" ")))
                        .questionId(item.getId())
                        .lessonId(item.getLesson().getId())
                        .build();
            }).toList();
        }

        return List.of();
    }

    @Override
    public List<RecentActivityDTO> getRecentActivities() {
        List<PlayerGame> playerGames = playerGameRepository.findRecentGames(PageRequest.of(0, 6));
        List<RecentActivityDTO> recentActivities = new ArrayList<>();
        for(PlayerGame pg: playerGames){
            Game game = gameRepository.findById(pg.getGameId().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Not found topic with id: " + pg.getId()));

            User user = userRepository.findById(pg.getUserId().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Not found topic with id: " + pg.getUserId()));
            String title = lessonRepository.findById(pg.getLesson().getId())
                    .map(Lesson::getTitle)
                    .orElse("");
            long minutesAgo = Duration.between(pg.getStartAt(), LocalDateTime.now()).toMinutes();
            String action = pg.isCompleted() ? "Completed" : "Started";
            recentActivities.add(
                    RecentActivityDTO
                            .builder()
                            .title(title)
                            .typeGame(game.getGameType().getName())
                            .action(action)
                            .avatar(user.getAvatar() != null ? user.getAvatar() : "")
                            .fullName(user.getFullName())
                            .minutesAgo(minutesAgo)
                            .build()
            );
        }
        return recentActivities;
    }

    @Override
    public void deleteGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Not found topic with id: " + gameId));
        gameRepository.delete(game);
    }

    private AnswerResultDTO checkAndCompleteGame(Long gameId, Long lessonId, Long playerId) {
        int answeredCount = playerAnswerRepository.countByPlayerIdAndTopicIdAndGameId(playerId, gameId);
        long totalQuestion = 0;
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Not found game with id: " + gameId));
        if (game.getGameType().getType().equals("MC") || game.getGameType().getType().equals("LS")) {
            totalQuestion = multipleChoiceGameQuestionRepository.countByGameIdAndLessonId(gameId, lessonId);
        } else {
            totalQuestion = arrangeSentenceRepository.countByGameIdAndLessonId(gameId, lessonId);
        }
        if (answeredCount >= totalQuestion) {
            int totalScore = playerAnswerRepository.sumPointByPlayerIdAndGameId(playerId, gameId);
            PlayerGame playerGame = playerGameRepository.findById(playerId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Not found player with id:" + playerId));
            playerGame.setCompleted(true);
            int streak = playerGame.getCurrentStreak();

            if(streak == totalQuestion){
                playerGame.setTotalScore(totalScore + 20);
            }else{
                playerGame.setTotalScore(totalScore);

            }
            playerGame.setCompletedAt(LocalDateTime.now());
            playerGameRepository.save(playerGame);
            return AnswerResultDTO.builder()
                    .totalScore(totalScore)
                    .complete(true)
                    .build();
        }
        return null;
    }
}
