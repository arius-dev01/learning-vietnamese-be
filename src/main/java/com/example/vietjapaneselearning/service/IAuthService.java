package com.example.vietjapaneselearning.service;

import com.example.vietjapaneselearning.dto.UserDTO;
import com.example.vietjapaneselearning.enums.RoleEnum;
import com.example.vietjapaneselearning.model.User;
import com.example.vietjapaneselearning.dto.request.AuthRequest;
import com.example.vietjapaneselearning.dto.request.RegisterRequest;
import com.example.vietjapaneselearning.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
    AuthResponse login(AuthRequest request, HttpServletRequest servletRequest, HttpServletResponse response);
    User register(RegisterRequest request);
    UserDTO addUser(UserDTO userDTO);
    void updateRole(Long userId, RoleEnum roleName);
    void deleteUser(Long userId);

}
