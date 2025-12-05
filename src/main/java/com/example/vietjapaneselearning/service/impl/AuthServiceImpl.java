package com.example.vietjapaneselearning.service.impl;

import com.example.vietjapaneselearning.dto.UserDTO;
import com.example.vietjapaneselearning.dto.request.AuthRequest;
import com.example.vietjapaneselearning.dto.request.RegisterRequest;
import com.example.vietjapaneselearning.dto.response.AuthResponse;
import com.example.vietjapaneselearning.enums.RoleEnum;
import com.example.vietjapaneselearning.model.DailyStreak;
import com.example.vietjapaneselearning.model.Role;
import com.example.vietjapaneselearning.model.Token;
import com.example.vietjapaneselearning.model.User;
import com.example.vietjapaneselearning.repository.DailyStreakRepository;
import com.example.vietjapaneselearning.repository.RoleRepository;
import com.example.vietjapaneselearning.repository.TokenRepository;
import com.example.vietjapaneselearning.repository.UserRepository;
import com.example.vietjapaneselearning.security.JwtUtils;
import com.example.vietjapaneselearning.service.IAuthService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements IAuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private DailyStreakRepository  dailyStreakRepository;
    @Autowired
    private CurrentUserService currentUserService;

    @Override
    public AuthResponse login(AuthRequest request, HttpServletRequest servletRequest, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("Account does not register!")
        );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Check password or email");
        }

        String refreshToken = null;
        if (servletRequest.getCookies() != null) {
            for (Cookie cookie : servletRequest.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        Optional<Token> existingToken = (refreshToken != null) ? tokenRepository.findRefreshToken(refreshToken) : Optional.empty();

        if (existingToken.isPresent() && existingToken.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            Token token = existingToken.get();
            refreshToken = token.getRefreshToken();
            long secondsLeft = Duration.between(LocalDateTime.now(), token.getExpiryDate()).getSeconds();
            Cookie cookie = new Cookie("refresh_token", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge((int) secondsLeft);
            response.addCookie(cookie);
        } else {
            refreshToken = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("refresh_token", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(cookie);
            LocalDateTime expiry = LocalDateTime.now().plusDays(7);
            Token newToken = Token.builder()
                    .refreshToken(refreshToken)
                    .expiryDate(expiry)
                    .user(user)
                    .build();
            tokenRepository.save(newToken);
        }
        return AuthResponse.builder()
                .access_token(jwtUtils.generateToken(user))
                .build();
    }

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (request.getPhoneNumber() != null && userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists!");
        }
        Optional<User> existEmail = userRepository.findByEmail(request.getEmail());
        if (existEmail.isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }
        Role role = roleRepository.findByName(RoleEnum.USER);
        User user = User
                .builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .birthDay(request.getBirthdate())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .language(request.getLanguage())
                .location(request.getLocation())
                .bio(request.getBio())
                .createdAt(LocalDateTime.now())
                .lastActiveDate(LocalDate.now())
                .role(role)
                .build();
        return userRepository.save(user);
    }

    @Override
    public UserDTO addUser(UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }
        Optional<User> checkPhone = userRepository.findByPhoneNumber(userDTO.getPhoneNumber());
        if (checkPhone.isPresent()) {
            throw new IllegalArgumentException("Phone number is already registered");
        }
        Role role = roleRepository.findByName(userDTO.getRoleName());

        User newUser = new User();
        newUser.setRole(role);
        newUser.setFullName(userDTO.getFullName());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPhoneNumber(userDTO.getPhoneNumber());
        newUser.setBio(userDTO.getBio());
        newUser.setLocation(userDTO.getLocation());
        newUser.setLanguage("English");
        newUser.setGender(userDTO.getGender());
        newUser.setPassword(passwordEncoder.encode("12345678"));
        newUser.setBirthDay(LocalDate.parse(userDTO.getBirthdate()));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setLastActiveDate(LocalDate.now());

        userRepository.save(newUser);

        return userDTO;
    }


    @Override
    public void updateRole(Long userId, RoleEnum roleName) {
        Role role = roleRepository.findByName(roleName);
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            existingUser.get().setRole(role);
        }
        userRepository.save(existingUser.get());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        Optional<User> existingUser = userRepository.findById(userId);
        DailyStreak dailyStreak = dailyStreakRepository.findByUser(existingUser.get());
        if(dailyStreak != null) {
            dailyStreakRepository.delete(dailyStreak);
        }

        userRepository.delete(existingUser.get());
    }

    @Override
    public UserDTO getUser() {
        User user = currentUserService.getUserCurrent();
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatar(user.getAvatar())
                .language(user.getLanguage())
                .roleName(user.getRole().getName())
                .build();
    }
}
