package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.QuestionRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.QuestionResponseDTO;
import com.nhattung.wogo.dto.response.QuestionUpdateResponseDTO;
import com.nhattung.wogo.service.question.IQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final IQuestionService questionService;


    @GetMapping("/all")
    public ApiResponse<PageResponse<QuestionResponseDTO>> getAllQuestions(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<QuestionResponseDTO>>builder()
                .result(questionService.getAllQuestions(page, size))
                .message("Successfully retrieved all questions")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<QuestionUpdateResponseDTO> getQuestionById(@PathVariable Long id) {
        return ApiResponse.<QuestionUpdateResponseDTO>builder()
                .result(questionService.getQuestionById(id))
                .message("Successfully retrieved question with ID: " + id)
                .build();
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<QuestionResponseDTO> saveQuestion(@RequestBody QuestionRequestDTO questionRequest) {
        return ApiResponse.<QuestionResponseDTO>builder()
                .result(questionService.saveQuestion(questionRequest))
                .message("Successfully saved question")
                .build();
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<QuestionResponseDTO> updateQuestion(@PathVariable Long id, @RequestBody QuestionRequestDTO questionRequest) {
        return ApiResponse.<QuestionResponseDTO>builder()
                .result(questionService.updateQuestion(id, questionRequest))
                .message("Successfully updated question with ID: " + id)
                .build();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ApiResponse.<Void>builder()
                .message("Successfully deleted question with ID: " + id)
                .build();
    }
}
