package com.example.vietjapaneselearning.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api/tts")
public class TTSController {
    @GetMapping
    public ResponseEntity<byte[]> getTss(@RequestParam String text) throws IOException {
        String encodedText = URLEncoder.encode(text, "UTF-8");
        String urlStr = "https://translate.google.com/translate_tts?ie=UTF-8&q=" + encodedText + "&tl=vi&client=tw-ob";
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        InputStream in = conn.getInputStream();
        byte[] bytes = in.readAllBytes();
        in.close();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("audio/mpeg"));
        return new  ResponseEntity<>(bytes, headers, HttpStatus.OK);

    }
}
