package com.example.vietjapaneselearning.dto.request;

import com.example.vietjapaneselearning.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotNull(message = "Full name cannot be null")
    @Size(min = 3, message = "Full name must be at least 3 characters")
    private String fullName;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email is invalid")
    private String email;
    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    private Gender gender;
    @NotNull(message = "Phone number cannot be null")
    @Pattern(regexp = "^(0[0-9]{9})$", message = "Phone number is not valid")
    @Size(min = 10, max = 11, message = "Phone number must be 10–11 digits")
    private String phoneNumber;
    @NotNull(message = "Location is required")
    private String location;
    @NotNull(message = "Language is required")
    private String language;
    private String bio;
    @Past(message = "Birthdate must be in the past")
    @NotNull(message = "Birthdate is required")
    private LocalDate birthdate;

}
