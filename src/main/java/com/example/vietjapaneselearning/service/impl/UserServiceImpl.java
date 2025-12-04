package com.example.vietjapaneselearning.service.impl;

import com.example.vietjapaneselearning.dto.UserDTO;
import com.example.vietjapaneselearning.enums.RoleEnum;
import com.example.vietjapaneselearning.model.Role;
import com.example.vietjapaneselearning.model.User;
import com.example.vietjapaneselearning.repository.RoleRepository;
import com.example.vietjapaneselearning.repository.UserRepository;
import com.example.vietjapaneselearning.service.IUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public UserDTO getCurrentUser() {
        User user = currentUserService.getUserCurrent();
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .location(user.getLocation())
                .language(user.getLanguage())
                .createdAt(user.getCreatedAt().toString())
                .bio(user.getBio())
                .birthdate(String.valueOf(user.getBirthDay()))
                .avatar(user.getAvatar() != null ? user.getAvatar() : "Unknow")
                .build();
    }

    @Override
    public User editProfileUser(UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findByEmail(currentUserService.getUserCurrent().getEmail());
        if (existingUser.isEmpty()) {
            throw new EntityNotFoundException("Not found profile with id " + existingUser.get().getId());
        }
        if (!userDTO.getPhoneNumber().equals(existingUser.get().getPhoneNumber()) && userRepository.findByPhoneNumber(userDTO.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone already exist in database!");

        }
        if (!userDTO.getEmail().equals(existingUser.get().getEmail()) && userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exist in database!");
        }
        if (userDTO.getPassword() != null && userDTO.getPassword().isBlank()  && !userDTO.getPassword().isEmpty() && !bCryptPasswordEncoder.matches(userDTO.getPassword(), existingUser.get().getPassword())) {
            throw new IllegalArgumentException("Password does incorrect");
        }
        existingUser.get().setFullName(userDTO.getFullName());
        existingUser.get().setPhoneNumber(userDTO.getPhoneNumber());
        existingUser.get().setEmail(userDTO.getEmail());
        existingUser.get().setAvatar(userDTO.getAvatar());
        existingUser.get().setLocation(userDTO.getLocation());
        existingUser.get().setBio(userDTO.getBio());
        existingUser.get().setUpdatedAt(LocalDateTime.now());
        if (userDTO.getNewPassword() != null && !userDTO.getNewPassword().isBlank()) {
            existingUser.get().setPassword(bCryptPasswordEncoder.encode(userDTO.getNewPassword()));
        }
        return userRepository.save(existingUser.get());
    }

    @Override
    public Page<UserDTO> getUsers(String keyword, RoleEnum role, Pageable pageable) {
        Page<User> userPage = userRepository.searchByKeywordAndRole(keyword, role, pageable);
        return userPage.map(
                item -> {
                    return UserDTO.builder()
                            .id(item.getId())
                            .fullName(item.getFullName())
                            .email(item.getEmail())
                            .phoneNumber(item.getPhoneNumber())
                            .gender(item.getGender())
                            .bio(item.getBio())
                            .location(item.getLocation())
                            .birthdate(String.valueOf(item.getBirthDay()))
                            .avatar(item.getAvatar())
                            .roleName(RoleEnum.valueOf(item.getRole().getName().toString()))
                            .build();
                }
        );

    }

    @Override
    public void translateLanguage(String language) {
        User user = currentUserService.getUserCurrent();
        user.setLanguage(language);
        userRepository.save(user);
    }

    @Override
    public UserDTO editUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userDTO.getId()));
        if (!user.getEmail().equals(userDTO.getEmail())) {
            Optional<User> checkEmail = userRepository.findByEmail(userDTO.getEmail());
            if (checkEmail.isPresent()) {
                throw new IllegalArgumentException("Email already exist in database!");
            }
            user.setEmail(userDTO.getEmail());
        }
        if (!user.getPhoneNumber().equals(userDTO.getPhoneNumber())) {
            Optional<User> checkPhone = userRepository.findByPhoneNumber(userDTO.getPhoneNumber());
            if (checkPhone.isPresent()) {
                throw new IllegalArgumentException("Phone already exist in database!");
            }
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        Role role = roleRepository.findByName(userDTO.getRoleName());
        if(role == null){
            throw new IllegalArgumentException("Not found role with name " + userDTO.getRoleName());
        }
        user.setGender(userDTO.getGender());
        user.setBio(userDTO.getBio());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRole(role);
        user.setLocation(userDTO.getLocation());
        user.setGender(userDTO.getGender());
        userRepository.save(user);
        return userDTO;
    }

    @Override
    public int countAll() {
        List<User> users = userRepository.findAll();
        return users.size();
    }

    @Override
    public int countByRole(RoleEnum role) {
        return userRepository.countByRole(role);
    }




}
