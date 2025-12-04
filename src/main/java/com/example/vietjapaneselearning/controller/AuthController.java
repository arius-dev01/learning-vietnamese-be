package com.example.vietjapaneselearning.controller;

import com.example.vietjapaneselearning.dto.UserDTO;
import com.example.vietjapaneselearning.dto.request.AuthRequest;
import com.example.vietjapaneselearning.dto.request.RegisterRequest;
import com.example.vietjapaneselearning.dto.response.AuthResponse;
import com.example.vietjapaneselearning.enums.RoleEnum;
import com.example.vietjapaneselearning.model.Token;
import com.example.vietjapaneselearning.model.User;
import com.example.vietjapaneselearning.repository.TokenRepository;
import com.example.vietjapaneselearning.security.JwtUtils;
import com.example.vietjapaneselearning.service.IAuthService;
import com.example.vietjapaneselearning.service.IPasswordResetService;
import com.example.vietjapaneselearning.service.impl.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
    public class AuthController {
    @Autowired
    private IAuthService authService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private IPasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest, HttpServletRequest servletRequest, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(authRequest,servletRequest, response));
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        List<Token> token = tokenRepository.findByUser(currentUserService.getUserCurrent());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        tokenRepository.deleteAll(token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(@CookieValue(value ="refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account does not login!");
        }
        Optional<Token> token = tokenRepository.findRefreshToken(refreshToken);
        if (token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");
        }
        User user = token.get().getUser();
        String newAccessToken = jwtUtils.generateToken(user);
        return ResponseEntity.ok(Map.of(
                "access_token", newAccessToken
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if(email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        try {
            passwordResetService.forgotPassword(email);
            return ResponseEntity.ok("Password reset link sent to your email");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send password reset email");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if(token == null || newPassword == null || token.isEmpty() || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Token and new password are required");
        }

        try {
            passwordResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password has been reset successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to reset password");
        }
    }

    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-role/{userId}/{role}")
    public ResponseEntity<?> updateRole(@PathVariable Long userId, @PathVariable RoleEnum role) {
        authService.updateRole(userId, role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        authService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(authService.addUser(userDTO));
    }

    @GetMapping("/getInfor")
    public ResponseEntity<?> getInfor() {
        return ResponseEntity.ok(authService.getUser());
    }
}
