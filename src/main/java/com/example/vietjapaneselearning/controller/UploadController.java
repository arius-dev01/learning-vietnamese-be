package com.example.vietjapaneselearning.controller;

import com.example.vietjapaneselearning.service.ICloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    @Autowired
    private ICloudinaryService cloudinaryService;

    @PostMapping()
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file)
            throws IOException {
        return ResponseEntity.ok(cloudinaryService.upload(file));
    }
}
