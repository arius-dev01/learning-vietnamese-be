package com.example.vietjapaneselearning.dto.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
