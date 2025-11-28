package com.example.vietjapaneselearning.controller;

import com.example.vietjapaneselearning.dto.UserDTO;
import com.example.vietjapaneselearning.enums.RoleEnum;
import com.example.vietjapaneselearning.model.User;
import com.example.vietjapaneselearning.service.IUserAchievementService;
import com.example.vietjapaneselearning.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/edit-profile")
    public ResponseEntity<User> editProfile(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.editProfileUser(userDTO));
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllUsers(@RequestParam(name = "keyword", required = false) String keyword,
                                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                                           @RequestParam(name = "role", required = false) RoleEnum role) {
        Map<String, Object> response = new HashMap<>();
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by("id").descending());
        Page<UserDTO> dtoPage = userService.getUsers(keyword, role, pageRequest);
        response.put("users", dtoPage.getContent());
        response.put("totalPage", dtoPage.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/translate-language")
    public ResponseEntity<?> translateLanguage(@RequestParam(name = "language", required = false) String language) {
        if(language == null){
            return ResponseEntity.badRequest().build();
        }
        userService.translateLanguage(language);
        return ResponseEntity.ok().build();
    }


}
