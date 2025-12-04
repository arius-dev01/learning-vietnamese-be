package com.example.vietjapaneselearning.controller;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.vietjapaneselearning.dto.QuestionDTO;
import com.example.vietjapaneselearning.service.IQuestionService;

@RestController
@RequestMapping("/api/question")
public class QuestionController {
    @Autowired
    private IQuestionService questionService;

    @PutMapping("/update/{gameId}/{lessonId}")
    public ResponseEntity<List<QuestionDTO>> updateQuestion(@PathVariable(name = "gameId") Long gameId,
                                                            @PathVariable(name = "lessonId") Long lessonId, @RequestBody List<QuestionDTO> questionDTO) {
        return ResponseEntity.ok(questionService.updateQuestion(gameId, lessonId, questionDTO));
    }

    @DeleteMapping("/delete/{gameId}/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable(name = "gameId") Long gameId,
                                               @PathVariable(name = "questionId") Long questionId) {
        questionService.deleteQuestion(questionId, gameId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/import-question-mc")
    public ResponseEntity<List<QuestionDTO>> importQuestionExcelMC(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(questionService.importExcelMultipleChoice(file));
    }

    @PostMapping("/import-question-lc")
    public ResponseEntity<List<QuestionDTO>> importQuestionExcelLC(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(questionService.importExcelListenChoice(file));
    }

    @PostMapping("/import-question-ar")
    public ResponseEntity<List<QuestionDTO>> importQuestionExcelAR(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(questionService.importExcelArrange(file));
    }

    @PostMapping("/add/{typeGame}/{lessonId}")
    public ResponseEntity<List<QuestionDTO>> addQuestion(@PathVariable(name = "typeGame") String typeGame,
                                                         @PathVariable(name = "lessonId") Long lessonId,
                                                         @RequestBody List<QuestionDTO> questionDTO) {
        return ResponseEntity.ok(questionService.addQuestion(typeGame, lessonId, questionDTO));
    }

    @GetMapping("/download-file-format")
    public ResponseEntity<byte[]> downloadFileFormat(@RequestParam String nameGame) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("File question format");
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            Row header = sheet.createRow(0);
            String[] columns = null;
            switch (nameGame) {
                case "AS":
                    columns = new String[]{
                            "Sentence",
                            "Explanation English",
                            "Explanation Japanese"
                    };
                    break;

                case "LS":
                    columns = new String[]{
                            "Question Text",
                            "Explanation English",
                            "Explanation Japanese",
                            "Option A",
                            "Option B",
                            "Option C",
                            "Option D",
                            "Option E",
                            "Option F",
                            "Option G",
                            "Correct Answer (A/B/C/D)"
                    };
                    break;

                case "MC":
                    columns = new String[]{
                            "Question Text English",
                            "Explanation English",
                            "Question Text Japan",
                            "Explanation Japanese",
                            "Option A",
                            "Option B",
                            "Option C",
                            "Option D",
                            "Correct Answer (A/B/C/D)"
                    };
                    break;
                default:
                    columns = new String[]{};
                    break;
            }

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);

            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=question-ar-format.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}
