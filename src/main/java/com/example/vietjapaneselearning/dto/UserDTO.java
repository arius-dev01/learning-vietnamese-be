package com.example.vietjapaneselearning.dto;

import com.example.vietjapaneselearning.enums.Gender;
import com.example.vietjapaneselearning.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String birthdate;
    private String password;
    private Gender gender;
    private String avatar;
    private String newPassword;
    private String phoneNumber;
    private String language;
    private String createdAt;
    private String location;
    private String bio;
    private RoleEnum roleName;
}
