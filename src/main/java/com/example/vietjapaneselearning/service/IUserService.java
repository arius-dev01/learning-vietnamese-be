package com.example.vietjapaneselearning.service;

import com.example.vietjapaneselearning.dto.UserDTO;
import com.example.vietjapaneselearning.enums.RoleEnum;
import com.example.vietjapaneselearning.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    UserDTO getCurrentUser();
    User editProfileUser(UserDTO userDTO);
    Page<UserDTO> getUsers(String nameOrEmail, RoleEnum role, Pageable pageable);
    void translateLanguage(String language);
    UserDTO editUser(UserDTO userDTO);
    int countAll();
    int countByRole(RoleEnum role);
}
