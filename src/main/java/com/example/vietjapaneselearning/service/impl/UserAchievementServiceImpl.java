package com.example.vietjapaneselearning.service.impl;

import com.example.vietjapaneselearning.dto.UserAchievementDTO;
import com.example.vietjapaneselearning.model.User;
import com.example.vietjapaneselearning.repository.PlayerGameRepository;
import com.example.vietjapaneselearning.service.IUserAchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAchievementServiceImpl implements IUserAchievementService {

    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private PlayerGameRepository playerGameRepository;
    @Override
    public UserAchievementDTO getUserAchievement() {
        User user = currentUserService.getUserCurrent();
        List<Long> totalScores = playerGameRepository.findMaxScoresByUser(user.getId());
        List<Long> totalGames = playerGameRepository.countGamesPlayed(user.getId());
        List<Long> totalLessons = playerGameRepository.countLessonPlayed(user.getId());
        Long totalLesson = totalLessons.stream().mapToLong(Long::longValue).sum();
        Long totalScore = totalScores.stream().mapToLong(Long::longValue).sum();
        Long totalGame =  totalGames.stream().mapToLong(Long::longValue).sum();
            return UserAchievementDTO.builder()
                .totalScore(totalScore)
                .totalGame(totalGame)
                .totalLesson(totalLesson)
                .build();
    }
}
