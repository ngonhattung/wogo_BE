package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.QuestionCategoryRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.QuestionCategoryResponseDTO;
import com.nhattung.wogo.dto.response.QuestionCategoryUpdateResponseDTO;
import com.nhattung.wogo.service.question.questioncategory.IQuestionCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/question-categories")
public class QuestionCategoryController {

    private final IQuestionCategoryService questionCategoryService;


    @GetMapping("/all")
    public ApiResponse<PageResponse<QuestionCategoryResponseDTO>> getAllQuestionCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<QuestionCategoryResponseDTO>>builder()
                .message("Fetched all question categories successfully")
                .result(questionCategoryService.getAllCategories(page, size))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<QuestionCategoryUpdateResponseDTO> getQuestionCategoryById(@PathVariable Long id) {
        return ApiResponse.<QuestionCategoryUpdateResponseDTO>builder()
                .message("Fetched question category successfully")
                .result(questionCategoryService.getCategoryById(id))
                .build();
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<QuestionCategoryResponseDTO> createQuestionCategory(
            @RequestBody QuestionCategoryRequestDTO questionCategoryRequest
    ) {
        return ApiResponse.<QuestionCategoryResponseDTO>builder()
                .message("Question category created successfully")
                .result(questionCategoryService.saveCategory(questionCategoryRequest))
                .build();
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<QuestionCategoryResponseDTO> updateQuestionCategory(
            @PathVariable Long id,
            @RequestBody QuestionCategoryRequestDTO questionCategoryRequest
    ) {
        return ApiResponse.<QuestionCategoryResponseDTO>builder()
                .message("Question category updated successfully")
                .result(questionCategoryService.updateCategory(id, questionCategoryRequest))
                .build();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Void> deleteQuestionCategory(@PathVariable Long id) {
        questionCategoryService.deleteCategory(id);
        return ApiResponse.<Void>builder()
                .message("Question category deleted successfully")
                .result(null)
                .build();
    }

}
