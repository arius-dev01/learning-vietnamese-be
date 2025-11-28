package com.example.vietjapaneselearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VietJapaneseLearningApplication {

	public static void main(String[] args) {
		SpringApplication.run(VietJapaneseLearningApplication.class, args);
	}

}
